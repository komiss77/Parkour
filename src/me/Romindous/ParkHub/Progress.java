package me.Romindous.ParkHub;


//прогресс игрока по каждой трассе
public class Progress {

    public int done; //прохождения
    public int checkPoint; //достигнутая точка на трассе
    
    public int trasseTime; //время затраченное на трассу
    public int trasseJump; //прыжки на трасся
    public int trasseFalls; //падения на трассе
    public boolean cheat; //было нарушение при прохождении
    
    public Progress() {
    }
    
    public Progress(final int done, final int checkPoint, final int trasseTime, final int trasseJump, final int trasseFalls, final boolean cheat) {
        this.checkPoint = checkPoint;
        this.trasseTime = trasseTime;
        this.trasseJump = trasseJump;
        this.trasseFalls = trasseFalls;
        this.cheat = cheat;
    }

    
    
    public void reset() {
        //done не трогаем!
        checkPoint = 0;
        trasseTime = 0;
        trasseJump = 0;
        trasseFalls = 0;
    }

    public boolean isZero() {
        return done==0 && checkPoint==0 && trasseTime ==0 && trasseJump==0 && trasseFalls==0 && !cheat;
    }

    
}
