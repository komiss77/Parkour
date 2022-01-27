package me.Romindous.ParkHub.builder;

import me.Romindous.ParkHub.Trasse;
import org.bukkit.entity.Player;
import ru.komiss77.builder.SetupMode;
import ru.komiss77.utils.inventory.SmartInventory;


public class LocalBuilder {

    public static void open(final Player p, final SetupMode sm) {
        
        if (sm.arena==null || sm.loacalEditMode==null) {
            
            sm.loacalEditMode = EditMode.Main;
            SmartInventory.builder()
                .id("MapSelect"+p.getName())
                .provider(new MapSelect(sm))
                .size(6, 9)
                .title("Выберите карту для редактора")
                .build()
                .open(p);
            
        } else if (sm.loacalEditMode == EditMode.Trasse) {
            openTrasseMenu(p, sm);
        }

    }

    public static void openTrasseMenu(final Player p, final SetupMode sm) {
        sm.loacalEditMode = EditMode.Trasse;
        SmartInventory.builder()
            .id("MapEdit"+p.getName())
            .provider(new MapEdit(sm))
            .size(6, 9)
            .title("Карта "+((Trasse)sm.arena).displayName)
            .build()
            .open(p);
    }
    
    
    
    
    
    
    
    
    
    
    
    public enum EditMode {
        Main, Trasse, Points 
        ;
    }
    
    
    
}
