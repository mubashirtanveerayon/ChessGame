package controller;

import elements.*;
import engine.Engine;
import server.move.Move;
import server.util.*;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedWriter;
import java.io.File;
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

    ArrayList<Move> movesMade;

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

        if(sidePanel.inf.isSelected()){
            for(int i=0;i<5;i++){
                if(sidePanel.depthButtons[i].isSelected()){
                    engine.setDepth(i+1);
                    break;
                }
            }
            sidePanel.toggleControl(false);
            Thread engineThread = engine.beginSearch();
            Thread listenerThread = new Thread(){
                @Override
                public void run(){
                    try {
                        engineThread.join();
                        Move move = engine.getEngineMove();
                        update(move);
                        if(!playerIsWhite){
                            boardPanel.buttons[7-move.locRank][7-move.locFile].setBackground(new Color(162, 16, 16));
                            boardPanel.buttons[7-move.destRank][7-move.destFile].setBackground(new Color(208, 15, 15));
                        }else{
                            boardPanel.buttons[move.locRank][move.locFile].setBackground(new Color(162, 16, 16));
                            boardPanel.buttons[move.destRank][move.destFile].setBackground(new Color(208, 15, 15));
                        }
                        sidePanel.toggleControl(true);
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            };
            listenerThread.start();
        }else{
            int movetime;
            if(sidePanel.two.isSelected()){
                movetime = 2000;
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
                    Move move = engine.getEngineMove();
                    update(move);
                    if(!playerIsWhite){
                        boardPanel.buttons[7-move.locRank][7-move.locFile].setBackground(new Color(162, 16, 16));
                        boardPanel.buttons[7-move.destRank][7-move.destFile].setBackground(new Color(208, 15, 15));
                    }else{
                        boardPanel.buttons[move.locRank][move.locFile].setBackground(new Color(162, 16, 16));
                        boardPanel.buttons[move.destRank][move.destFile].setBackground(new Color(208, 15, 15));
                    }
                    sidePanel.toggleControl(true);
                }
            };
            listenerThread.start();
        }
    }

    private void update(Move move){
        if(onMoveIndex != movesMade.size()){
            for(int i=movesMade.size()-1;i>=onMoveIndex;i--){
                movesMade.remove(i);
            }
            sidePanel.moveListArea.setText("");
            String moveText = PGNUtils.generateSANMoveText(movesMade);
            sidePanel.moveListArea.setText(moveText);
        }

        String pgn = PGNUtils.cvt(move,engine.mm);
        if(!engine.cb.whiteToMove){
            sidePanel.moveListArea.append(" "+pgn);
        }else{
            sidePanel.moveListArea.append(" "+engine.cb.fullMoveClock+". "+pgn);
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
            for(Move move:engine.getLegalMoves()){
                if(move.toString().equalsIgnoreCase(promotionMove)){
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
                            if(playerIsWhite && engine.cb.whiteToMove && engine.cb.board[i][j] != Constants.EMPTY_SQUARE && Character.isUpperCase(engine.cb.board[i][j])) {
                                selectedSquareIndex = j + i * 8;
                                ArrayList<Move> moves = getMoves(j,i);
                                if(moves != null) {
                                    for (Move move : moves) {
                                        if(move.isKingSideCastling || move.isQueenSideCastling){
                                            if (move.isQueenSideCastling) {
                                                boardPanel.buttons[7][2].setBackground(new Color(30,129,176));
                                            }else {
                                                boardPanel.buttons[7][6].setBackground(new Color(30,129,176));
                                            }
                                        }else {
                                            boardPanel.buttons[move.destRank][move.destFile].setBackground(new Color(30,129,176));
                                        }
                                    }
                                }
                                if(i == 1  && engine.cb.board[i][j] == Constants.WHITE_PAWN){
                                    for(Move move:engine.mm.getAllMoves()){
                                        if(move.promotionPiece != Constants.EMPTY_SQUARE){
                                            hasPromotion = true;
                                            break;
                                        }
                                    }
                                }
                            }else if(!playerIsWhite &&!engine.cb.whiteToMove &&  engine.cb.board[7-i][7-j] != Constants.EMPTY_SQUARE && !Character.isUpperCase(engine.cb.board[7-i][7-j])){
                                selectedSquareIndex = (7-j)+(7-i) * 8;
                                ArrayList<Move> moves = getMoves(7-j,7-i);
                                if(moves != null) {
                                    for (Move move : moves) {
                                        if(move.isKingSideCastling || move.isQueenSideCastling){
                                            if (move.isQueenSideCastling) {
                                                boardPanel.buttons[7][5].setBackground(new Color(30,129,176));
                                            }else{
                                                boardPanel.buttons[7][1].setBackground(new Color(30,129,176));
                                            }
                                        }else {
                                            boardPanel.buttons[7-move.destRank][7-move.destFile].setBackground(new Color(30,129,176));
                                        }
                                    }
                                }
                                if(i == 1  && engine.cb.board[7-i][7-j] == Constants.BLACK_PAWN){
                                    for(Move move:engine.mm.getAllMoves()){
                                        if(move.promotionPiece != Constants.EMPTY_SQUARE){
                                            hasPromotion = true;
                                            break;
                                        }
                                    }
                                }
                            }
                        }else {
                            boardPanel.render(engine.cb.board,!playerIsWhite);
                            if(playerIsWhite){
                                if(j+i*8 != selectedSquareIndex){
                                    String algebric = Util.cvtCoord(selectedSquareIndex)+Util.cvtCoord(j+i*8);
                                    if(hasPromotion){
                                        promotionWindow.show(playerIsWhite);
                                        promotionMove = algebric;
                                        sidePanel.toggleControl(false);
                                        return;
                                    }

                                    for(Move move:engine.mm.getAllMoves()){
                                        if(move.toString().equalsIgnoreCase(algebric)){
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
                                    for(Move move:engine.mm.getAllMoves()){
                                        if(move.toString().equalsIgnoreCase(algebric)){
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

            if(src == sidePanel.two){
                for(JRadioButton depthButton:sidePanel.depthButtons){
                    depthButton.setEnabled(false);
                }
            }else if(src == sidePanel.five){
                for(JRadioButton depthButton:sidePanel.depthButtons){
                    depthButton.setEnabled(false);
                }
            }else if(src == sidePanel.ten){
                for(JRadioButton depthButton:sidePanel.depthButtons){
                    depthButton.setEnabled(false);
                }
            }else if(src == sidePanel.inf){
                boolean depthSelected = false;
                for(JRadioButton depthButton:sidePanel.depthButtons){
                    depthButton.setEnabled(true);
                    if(depthButton.isSelected()){
                        depthSelected = true;
                    }
                }
                if(!depthSelected){
                    sidePanel.depthButtons[2].setSelected(true);
                }
            }else {

                playerIsWhite = sidePanel.playAsW.isSelected();
                selectedSquareIndex = -1;
                hasPromotion = false;
                boardPanel.render(engine.cb.board, !playerIsWhite);
                if (playerIsWhite) {
                    if (!engine.cb.whiteToMove) {
                        playComputer();
                    }
                } else {
                    if (engine.cb.whiteToMove) {
                        playComputer();
                    }
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
                JFileChooser fileChooser = getPGNFileSelector();
                int res = fileChooser.showOpenDialog(null);
                if(res == JFileChooser.APPROVE_OPTION){
                    String path = fileChooser.getSelectedFile().getPath();
                    engine = new Engine();
                    movesMade.clear();
                    onMoveIndex = 0;
                    ArrayList<HashMap<String,String>> content = PGNUtils.parsePGNFile(path,1);
                    String moveText = content.get(0).get("Moves");
                    for(String seg:moveText.split(" ")){
                        if(!Character.isDigit(seg.charAt(0))){
                            Move move = PGNUtils.parse(seg,engine.mm);
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
                text += "[Result \""+result+"\"]\n";
                int plyCount = (engine.cb.fullMoveClock) * 2;
                if(!engine.cb.whiteToMove){
                    plyCount++;
                }
                text += "[PlyCount \""+String.valueOf(plyCount)+"\"]\n";
                text += sidePanel.moveListArea.getText();
                text += " " + result;
                try(BufferedWriter br = new BufferedWriter(new FileWriter(path))){
                    br.write(text);
                }catch(IOException e){
                    e.printStackTrace();
                }

            }else if(src == loadPosition){
                String fen = JOptionPane.showInputDialog(null,"FEN string:");
                if(fen == null)return;
                engine = new Engine(fen);
                movesMade.clear();
                onMoveIndex = 0;
                sidePanel.moveListArea.setText("");
                boardPanel.render(engine.cb.board,!playerIsWhite);
                if((engine.cb.whiteToMove && !playerIsWhite) || (!engine.cb.whiteToMove && playerIsWhite)){
                    playComputer();
                }
            }else if(src == copyFen){
                Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                String fen = FenUtils.generate(engine.cb);
                StringSelection content = new StringSelection(fen);
                clip.setContents(content,content);
            }
        }
    }


    private ArrayList<Move> getMoves(final int file,final int rank){

        return engine.mm.generateMove(file,rank);
    }

    private String getResult(){
        if(engine.mm.getAllMoves().isEmpty()) {
            if (engine.cb.gs == GameState.CHECK) {
                if(!engine.cb.whiteToMove){
                    return "1-0";
                }else{
                    return "0-1";
                }
            }else{
                return "1/2-1/2";
            }
        }else if(engine.cb.halfMoveClock == 100){
            return "1/2-1/2";
        }
        return "*";
    }

    public static JFileChooser getPGNFileSelector(){


        JFileChooser fileChooser =  new JFileChooser();
        fileChooser.addChoosableFileFilter(new FileNameExtensionFilter("PGN Files", "pgn"));
        fileChooser.removeChoosableFileFilter(fileChooser.getChoosableFileFilters()[0]);

        return fileChooser;
    }


}
