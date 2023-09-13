package controller;

import elements.*;
import engine.Engine;
import server.util.*;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;

public class GameController implements ActionListener {

    BoardPanel boardPanel;
    SidePanel sidePanel;

    JMenuBar menuBar;
    Engine engine;

    LocalDateTime dateTime;

    DateTimeFormatter formatter;

    boolean playerIsWhite = true,hasPromotion = false;

    JMenuItem newGame,loadGame,saveGame,loadPosition,copyFen;

    int selectedSquareIndex=-1;

    ArrayList<String> movesMade;

    PromotionChooser promotionWindow;

    String promotionMove;

    int onMoveIndex;

    final String username = System.getProperty("user.name");

    public GameController(BoardPanel b,SidePanel s,JMenuBar mb){
        boardPanel = b;
        sidePanel = s;
        menuBar = mb;
        engine = new Engine();
        boardPanel.registerListener(this);
        sidePanel.registerListener(this);
        movesMade = new ArrayList<>();

        formatter = DateTimeFormatter.ofPattern("dd.mm.yyyy");

        promotionWindow = new PromotionChooser();
        promotionWindow.registerListener(this);
        configureMenu();
        boardPanel.render(engine.cb.board,!playerIsWhite);
    }

    private void configureMenu() {
        JMenu menu = new JMenu("File");
        newGame = new JMenuItem("New Game");
        loadGame = new JMenuItem("Load Game");
        saveGame = new JMenuItem("Save (PGN)");
        loadPosition = new JMenuItem("Load Position from FEN");
        copyFen = new JMenuItem("Copy FEN to clipboard");

        menu.add(newGame);
        menu.add(loadGame);
        menu.add(saveGame);
        menu.add(loadPosition);
        menu.add(copyFen);

        menuBar.add(menu);

        for(int i=0;i<5;i++){
            menu.getItem(i).addActionListener(this);
        }
    }

    private void playComputer(){
        if(engine.getLegalMoves().isEmpty()){
            return;
        }
        for(int i=0;i<5;i++){
            if(sidePanel.depthButtons[i].isSelected()){
                engine.setDepth(i+1);
                break;
            }
        }
        if(sidePanel.inf.isSelected()){
            sidePanel.toggleControl(false);
            Thread engineThread = engine.beginSearch();
            Thread listenerThread = new Thread(){
                @Override
                public void run(){
                    try {
                        engineThread.join();
                        update(engine.getEngineMove());
                        sidePanel.toggleControl(true);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            listenerThread.start();
        }else{
            int movetime;
            if(sidePanel.seven.isSelected()){
                movetime = 7000;
            }else if(sidePanel.ten.isSelected()){
                movetime = 10000;
            }else{
                movetime = 5000;
            }
            sidePanel.toggleControl(false);
            engine.beginSearch(movetime);
            Thread listenerThread = new Thread(){
                @Override
                public void run(){
                    while(engine.isSearching()){
                        System.out.print("");
                    }
                    update(engine.getEngineMove());
                    sidePanel.toggleControl(true);
                }
            };
            listenerThread.start();
        }
    }

    private void update(String move){
        if(onMoveIndex != movesMade.size()){
            for(int i=movesMade.size()-1;i>=onMoveIndex;i--){
                movesMade.remove(i);
            }
            sidePanel.moveListArea.setText("");
            String moveText = PGNUtils.getMoveText(movesMade);
            sidePanel.moveListArea.setText(moveText);
        }

        String pgn = PGNUtils.cvt(move,engine.mm);
        if(engine.cb.turn == Constants.BLACK){
            sidePanel.moveListArea.append(" "+pgn);
        }else{
            sidePanel.moveListArea.append(" "+engine.cb.fenParts[12]+". "+pgn);
        }

        onMoveIndex ++;
        engine.makeMove(move);
        movesMade.add(move);
        boardPanel.render(engine.cb.board,!playerIsWhite);
    }



    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        Object src = actionEvent.getSource();
        if(promotionWindow.isVisible()){
            if(src == promotionWindow.qButton){
                promotionMove += Constants.WHITE_QUEEN;
            }else if(src == promotionWindow.rButton){
                promotionMove += Constants.WHITE_ROOK;
            }else if(src == promotionWindow.bButton){
                promotionMove += Constants.WHITE_BISHOP;
            }else if(src == promotionWindow.nButton){
                promotionMove += Constants.WHITE_KNIGHT;
            }
            for(String move:engine.mm.getAllMoves()){
                if(engine.mm.cvt(move).equalsIgnoreCase(promotionMove)){
                    update(move);
                    playComputer();
                    break;
                }
            }

            hasPromotion = false;
            promotionWindow.hide();
            sidePanel.toggleControl(true);
        }else if(src instanceof JButton) {
            if(src == sidePanel.prevButton && onMoveIndex>0){
                onMoveIndex--;
                engine.undoMove(movesMade.get(onMoveIndex));
                boardPanel.render(engine.cb.board,!playerIsWhite);
                return;
            }else if(src == sidePanel.nextButton && onMoveIndex < movesMade.size()){
                onMoveIndex ++;
                engine.makeMove(movesMade.get(onMoveIndex-1));
                boardPanel.render(engine.cb.board,!playerIsWhite);
                return;
            }
            for (int i = 0; i < 8; i++) {
                for (int j = 0; j < 8; j++) {
                    if (src == boardPanel.buttons[i][j]) {
                        if(selectedSquareIndex < 0){
                            if(playerIsWhite && engine.cb.turn == Constants.WHITE && engine.cb.board[i][j] != Constants.EMPTY_SQUARE && Character.isUpperCase(engine.cb.board[i][j])) {
                                selectedSquareIndex = j + i * 8;
                                if(i == 1  && engine.cb.board[i][j] == Constants.WHITE_PAWN){
                                    for(String move:engine.mm.getAllMoves()){
                                        if(move.split(Constants.MOVE_SEPARATOR).length == Constants.PROMOTION_MOVE_LENGTH){
                                            hasPromotion = true;
                                            break;
                                        }
                                    }
                                }
                            }else if(!playerIsWhite &&engine.cb.turn == Constants.BLACK &&  engine.cb.board[7-i][7-j] != Constants.EMPTY_SQUARE && !Character.isUpperCase(engine.cb.board[7-i][7-j])){
                                selectedSquareIndex = (7-j)+(7-i) * 8;
                                if(i == 1  && engine.cb.board[7-i][7-j] == Constants.BLACK_PAWN){
                                    for(String move:engine.mm.getAllMoves()){
                                        if(move.split(Constants.MOVE_SEPARATOR).length == Constants.PROMOTION_MOVE_LENGTH){
                                            hasPromotion = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }else {
                            if(playerIsWhite){
                                if(j+i*8 != selectedSquareIndex){
                                    String algebric = Util.cvtCoord(selectedSquareIndex)+Util.cvtCoord(j+i*8);
                                    if(hasPromotion){
                                        promotionWindow.show(playerIsWhite);
                                        promotionMove = algebric;
                                        sidePanel.toggleControl(false);
                                        return;
                                    }

                                    for(String move:engine.mm.getAllMoves()){
                                        if(engine.mm.cvt(move).equalsIgnoreCase(algebric)){
                                            update(move);
                                            playComputer();
                                            break;
                                        }
                                    }
                                }
                            }else{
                                if((7-j)+(7-i)*8 != selectedSquareIndex){
                                    String algebric = Util.cvtCoord(selectedSquareIndex)+Util.cvtCoord((7-j)+(7-i)*8);
                                    if(hasPromotion){
                                        promotionWindow.show(playerIsWhite);
                                        promotionMove = algebric;
                                        sidePanel.toggleControl(false);
                                        return;
                                    }
                                    for(String move:engine.mm.getAllMoves()){
                                        if(engine.mm.cvt(move).equalsIgnoreCase(algebric)){
                                            update(move);
                                            playComputer();
                                            break;
                                        }
                                    }
                                }
                            }
                            selectedSquareIndex = -1;
                            hasPromotion = false;
                        }
                    }
                }
            }
        }else if(src instanceof JRadioButton){
            if(engine.isSearching()){
                sidePanel.playAsW.setSelected(playerIsWhite);
                sidePanel.playAsB.setSelected(!playerIsWhite);
                return;
            }
            playerIsWhite = sidePanel.playAsW.isSelected();
            selectedSquareIndex = -1;
            hasPromotion = false;
            boardPanel.render(engine.cb.board,!playerIsWhite);
            if(playerIsWhite){
                if(engine.cb.turn == Constants.BLACK){
                    playComputer();
                }
            }else{
                if(engine.cb.turn == Constants.WHITE){
                    playComputer();
                }
            }
        }else if(src instanceof JMenuItem){
            if(src == newGame){
                engine = new Engine();
                sidePanel.moveListArea.setText("");
                boardPanel.render(engine.cb.board,!playerIsWhite);
                movesMade.clear();
                onMoveIndex = 0;
                if(!playerIsWhite){
                    playComputer();
                }
            }else if(src == loadGame){
                JFileChooser fileChooser = new JFileChooser();
                int res = fileChooser.showOpenDialog(null);
                if(res == JFileChooser.APPROVE_OPTION){
                    String path = fileChooser.getSelectedFile().getPath();
                    engine = new Engine();
                    movesMade.clear();
                    onMoveIndex = 0;
                    ArrayList<HashMap<String,String>> content = PGNUtils.parseFile(path,1);
                    String moveText = content.get(0).get("Moves");
                    for(String seg:moveText.split(" ")){
                        if(!Character.isDigit(seg.charAt(0))){
                            String move = PGNUtils.parse(seg,engine.mm);
                            if(move!=null){
                                update(move);
                            }
                        }
                    }
                }
            }else if(src == saveGame){
                String path = JOptionPane.showInputDialog(null,"PGN filename:");
                if(!path.endsWith(".pgn")){
                    path += ".pgn";
                }
                String text = "[Event \"Chess game\"]" +
                        "\n[Date \"";
                dateTime = LocalDateTime.now();
                String date = dateTime.format(formatter);
                text += date+"\"]";
                text += "\n[White ";
                if(playerIsWhite){
                    text += "\""+username;
                }else{
                    text += "\"Schneizel v2";
                }
                text += "\"]\n[Black ";
                if(playerIsWhite){
                    text += "\"Schneizel v2";
                }else{
                    text += "\""+username;
                }
                text += "\"]\n";
                String result = getResult();
                if(!result.equalsIgnoreCase("Continue")){
                    text += "[Result \""+result+"\"]\n";
                }
                int plyCount = Integer.parseInt(engine.cb.fenParts[12]) * 2;
                if(engine.cb.turn == Constants.BLACK){
                    plyCount++;
                }
                text += "[PlyCount \""+String.valueOf(plyCount)+"\"]\n";
                text += sidePanel.moveListArea.getText();
                try(BufferedWriter br = new BufferedWriter(new FileWriter(path))){
                    br.write(text);
                }catch(IOException e){
                    e.printStackTrace();
                }

            }else if(src == loadPosition){
                String fen = JOptionPane.showInputDialog(null,"FEN string:");
                engine = new Engine(fen);
                movesMade.clear();
                onMoveIndex = 0;
                sidePanel.moveListArea.setText("");
                boardPanel.render(engine.cb.board,!playerIsWhite);
            }else if(src == copyFen){
                Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                String fen = FenUtils.cat(engine.cb.fenParts);
                StringSelection content = new StringSelection(fen);
                clip.setContents(content,content);
            }
        }
    }


    private String getResult(){
        if(engine.mm.getAllMoves().isEmpty()) {
            if (engine.cb.gs == GameState.CHECK) {
                if(engine.cb.turn == Constants.BLACK){
                    return "1-0";
                }else{
                    return "0-1";
                }
            }else{
                return "1/2-1/2";
            }
        }else if(Integer.parseInt(engine.cb.fenParts[11]) == 100){
            return "1/2-1/2";
        }
        return "Continue";
    }

}
