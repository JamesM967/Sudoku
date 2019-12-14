package view;

public class Note {

    private static final int IS_NOTED = 1;
    private static final int IS_NOT_NOTED = 0;

    private int[] notes;

    public Note(int dimension) {
        notes = new int[dimension];
    }

    public boolean hasNote(int notedNumber) {
        return isInBounds(notedNumber) && notes[notedNumber] == IS_NOTED;
    }

    public void addNumber(int numToNote) {
        if (isInBounds(numToNote)) {
            notes[numToNote] = IS_NOTED;
        }
    }

    public void removeNumber(int numToRemove) {
        if (isInBounds(numToRemove)) {
            notes[numToRemove] = IS_NOT_NOTED;
        }
    }

    private boolean isInBounds(int notedNumber) {
        return notedNumber > 0 && notedNumber < notes.length;
    }

}
