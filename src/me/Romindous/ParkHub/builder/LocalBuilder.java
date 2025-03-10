package me.Romindous.ParkHub.builder;

import me.Romindous.ParkHub.CheckPoint;
import me.Romindous.ParkHub.Main;
import me.Romindous.ParkHub.PD;
import me.Romindous.ParkHub.Trasse;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import ru.komiss77.builder.SetupMode;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.inventory.SmartInventory;


public class LocalBuilder {

    public static void open(final Player p, final SetupMode sm) {
        Main.select.takeAway(p);//p.getInventory().setItem(1, new ItemStack(Material.AIR)); //убрать выбор паркура
        Main.toStatr.takeAway(p);//p.getInventory().setItem(7, new ItemStack(Material.AIR)); //убрать выход в лобби
        Main.suicide.takeAway(p);//
        Main.exit.takeAway(p);//
        Main.leave.takeAway(p);//
        
        
        final PD pd = Main.data.get(p.getName());
        if (pd.current!=null) {
            pd.resetTrasse(true);
        }
        PM.getOplayer(pd.name).score.getSideBar().reset();
        if (sm.arena==null || sm.loacalEditMode==null || sm.loacalEditMode == EditMode.Main) {
            
            sm.loacalEditMode = EditMode.Main;
            SmartInventory.builder()
                .id("MapSelect"+p.getName())
                .provider(new TrasseSelect(sm))
                .size(6, 9)
                .title("Выберите карту для редактора")
                .build()
                .open(p);
            
        } else if (sm.loacalEditMode == EditMode.Trasse) {
            
            openTrasseEditor(p, sm);
            
        } else if (sm.loacalEditMode == EditMode.Points) {
            
            openCheckPointEditor(p, sm);
            
        }

    }

    public static void openTrasseEditor(final Player p, final SetupMode sm) {
        sm.loacalEditMode = EditMode.Trasse;
        SmartInventory.builder()
            .id("TrasseEditor"+p.getName())
            .provider(new TrasseEdit(sm))
            .size(2, 9)
            .title("Карта "+((Trasse)sm.arena).displayName)
            .build()
            .open(p);
    }
    
    
    public static void openCheckPointEditor(final Player p, final SetupMode sm) {
        sm.loacalEditMode = EditMode.Points;
        SmartInventory.builder()
            .id("CheckPointEditor"+p.getName())
            .provider(new PointsEdit(sm))
            .size(6, 9)
            .title("Карта "+((Trasse)sm.arena).displayName)
            .build()
            .open(p);
    }

    public static void openPointSettings(final Player p, final SetupMode sm, final CheckPoint cp) {
        SmartInventory.builder()
            .id("PointSettings"+p.getName())
            .provider(new PointSettings(sm, cp))
            .type(InventoryType.HOPPER)
            .title("Настройки чекпоинта")
            .build()
            .open(p);
    }
        
    
    
    
    
    
    
    
    public enum EditMode {
        Main, Trasse, Points 
        ;
    }
    
    
    
}
