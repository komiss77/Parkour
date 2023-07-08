package me.Romindous.ParkHub;


import java.util.List;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;



public class TopMenu implements InventoryProvider {
    
    
    
    private final List<ItemStack> topList;
    //private final Island is;
    
    private static final ItemStack fill = new ItemBuilder(Material.LIME_STAINED_GLASS_PANE).name("§8.").build();;

    
    public TopMenu(final List<ItemStack> topList) {
        this.topList = topList;
        //this.is = is;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent contents) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        contents.set(0, 1, ClickableItem.empty(fill));
        contents.set(0, 7, ClickableItem.empty(fill));
        contents.set(1, 0, ClickableItem.empty(fill));
        contents.set(1, 8, ClickableItem.empty(fill));
        
        
        
        
       /* contents.set( 0, 0, ClickableItem.of( new ItemBuilder(Material.REPEATER)
                .name("§fКритерий лидерства")
                .addLore("")
                .addLore("§7Сейчас: §b"+topType.displayName)
                .addLore("")
                .addLore("§7ЛКМ - показать ТОП по "+topType.next(topType).displayName)
                .addLore("")
                .build(), e -> {
            
                    MenuManager.openTop(player, TopType.next(topType));

                }
        ));*/
        
        
        
        
        contents.set( 0, 8, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR).name("закрыть").build(), e -> 
            p.performCommand("pk")
        ));
        
        
        
        
        
        
        int startRow = 4;
        
        for (int line=0; line <6; line++) {
            
            for (int column=startRow; column<9; column++) {
            
                if (topList.isEmpty()) {
                    contents.set(line, column, ClickableItem.empty(new ItemBuilder(Material.FIREWORK_STAR)
                        .name("§8Место свободно!")
                        .build()
                    )); 
                } else {
                    contents.set(line, column, ClickableItem.empty(topList.get(0)));
                    topList.remove(0);
                }
                
                if (line!=5)column++; //до шестой строки добавлять через 1
                
                if (line==0) break;
                else if (line==1 && column>=5) break;
                else if (line==2 && column>=6) break;
                else if (line==3 && column>=7) break;
            
            }
        
            if (startRow>0)startRow-=1;
        }
        
        
            
            
        
        
        

        
        
            
            
        
        
        
        
        
        
        
        
        

        

        
        
        

        

        
        

        
        

    }
    
    
    
    
    
    
    
    
    
    
}
