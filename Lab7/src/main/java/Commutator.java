public class Commutator {
    private String leftBound;
    private String rightBound;
    private long time;

    Commutator(String leftBound, String rightBound, long time){
        this.time = time;
        this.leftBound = leftBound;
        this.rightBound = rightBound;
    }
    public String getLeftBound() {
        return leftBound;
    }

    public String getRightBound() {
        return rightBound;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public boolean intersect(String value){
        return Integer.parseInt(leftBound) <= Integer.parseInt(value) && Integer.parseInt(value) >= Integer.parseInt(rightBound);
    }
}
