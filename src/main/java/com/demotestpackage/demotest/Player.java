package com.demotestpackage.demotest;

// Abstract Player Class
abstract class Player {
    private final String name;
    private final char pieceSymbol;
    private final char kingSymbol;


    public Player(String name, char pieceSymbol, char kingSymbol) {
        this.name = name;
        this.pieceSymbol = pieceSymbol;
        this.kingSymbol = kingSymbol;
    }

    public String getName() {
        return name;
    }

    public char getPieceSymbol() {
        return pieceSymbol;
    }

    public char getKingSymbol() {
        return kingSymbol;
    }

    public abstract boolean isPlayerTurn();


}
