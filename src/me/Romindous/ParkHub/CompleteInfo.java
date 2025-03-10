package me.Romindous.ParkHub;


//инфо о прохождении
public class CompleteInfo {

    public int done; //прохождения
    public int trasseTime; //время затраченное на трассу
    public int trasseJump; //прыжки на трасся
    public int trasseFalls; //падения на трассе

    public CompleteInfo(final int done, final int trasseTime, final int trasseJump, final int trasseFalls) {
        this.done = done;
        this.trasseTime = trasseTime;
        this.trasseJump = trasseJump;
        this.trasseFalls = trasseFalls;
    }

    
}
