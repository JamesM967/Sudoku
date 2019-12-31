package config;

public enum Difficulty {
    EASY("Easy", 40), MEDIUM("Medium", 50), HARD("Hard", 60);

    private String name;
    private int numSquaresLeftOpen;

    Difficulty(String name, int numSquaresLeftOpen) {
        this.name = name;
        this.numSquaresLeftOpen = numSquaresLeftOpen;
    }


    public String getName() {
        return name;
    }

    public int getNumSquaresLeftOpen() {
        return numSquaresLeftOpen;
    }
}
