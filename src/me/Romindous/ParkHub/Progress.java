package me.Romindous.ParkHub;


//прогресс игрока по каждой трассе
public class Progress {

    public final int hash;
    public int checkPoint; //достигнутая точка на трассе
    
    public int trasseTime; //время затраченное на трассу
    public int trasseJump; //прыжки на трасся
    public int trasseFalls; //падения на трассе
    public boolean cheat; //было нарушение при прохождении
    
    public Progress(final int hash) {
        this.hash = hash;
    }
    
    public Progress(final int hash, final int checkPoint, final int trasseTime, final int trasseJump, final int trasseFalls, final boolean cheat) {
        this.hash = hash;
        this.checkPoint = checkPoint;
        this.trasseTime = trasseTime;
        this.trasseJump = trasseJump;
        this.trasseFalls = trasseFalls;
        //this.done = done;
        this.cheat = cheat;
    }

    
    
    public void reset() {
        //done не трогаем!
        //cheat не трогаем!
        checkPoint = 0;
        trasseTime = 0;
        trasseJump = 0;
        trasseFalls = 0;
    }

    public boolean haProgress() {
        return checkPoint>0 || trasseTime > 0 || trasseJump > 0 || trasseFalls > 0;
    }

    
}
