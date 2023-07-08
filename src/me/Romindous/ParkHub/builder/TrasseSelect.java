package me.Romindous.ParkHub.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.builder.SetupMode;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;
import me.Romindous.ParkHub.Main;
import me.Romindous.ParkHub.Trasse;
import org.bukkit.inventory.meta.ItemMeta;
import ru.komiss77.Timer;









public class TrasseSelect implements InventoryProvider{

    //private static final ItemStack fill = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).build();;
    private final SetupMode sm;
    
    TrasseSelect(final SetupMode sm) {
        this.sm = sm;
    }
        
    
    
    @Override
    public void init(final Player p, final InventoryContent content) {
        
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        
        final Pagination pagination = content.pagination();
        final ArrayList<ClickableItem> entry = new ArrayList<>();         
        

        
        for (Trasse t : Main.trasses.values()) {
            
            final List<String> lore = Arrays.asList(
                t.disabled ? "§4Выключен" : "§2Включен",
                "§3Мир: "+t.worldName,
                "§7Создан: §3"+ApiOstrov.dateFromStamp(t.createAt),
                "§7Создатель: §f"+t.creator,
                "§7Сложность: §5"+t.level.name(),
                t.descr,
                t.inProgress.isEmpty() ? "§7никого нет" : ("§7Проходят: "+t.inProgress.size()),
                "§7⚐: "+t.size(),
                "§7Пройден: "+t.totalDone+" раз.",
                "§7⌚ "+ApiOstrov.secondToTime(t.totalTime),
                "§7⇪: "+t.totalFalls,
                "§7☠: "+t.totalJumps,
                "§7ЛКМ - редактировать"
            );

            final ItemStack is = new ItemStack(t.mat);
            final ItemMeta im = is.getItemMeta();
            im.setDisplayName(t.displayName);
            im.setLore(lore);
            is.setItemMeta(im);
            
            /*final ItemStack is = new ItemBuilder(t.mat)
                .name(t.displayName)
                .addLore("t.disabled ? "§4Выключен" : "§2Включен")
                .addLore("§3Мир: "+t.worldName)
                .addLore("§7Создан: §3"+ApiOstrov.dateFromStamp(t.createAt))
                .addLore("§7Создатель: §f"+t.creator)
                .addLore("§7Сложность: §5"+t.level.name())
                .addLore("t.descr)
                .addLore("t.inProgress.isEmpty() ? "§7никого нет" : ("§7Проходят: "+t.inProgress.size()) )
                .addLore("§7⚐: "+t.size())
                .addLore("§7Пройден: "+t.totalDone+" раз.")
                .addLore("§7⌚ "+ApiOstrov.secondToTime(t.totalTime))
                .addLore("§7⇪: "+t.totalFalls)
                .addLore("§7☠: "+t.totalJumps)
                .addLore("§7ЛКМ - редактировать")
                .build();*/
            
            entry.add(ClickableItem.of(is, e-> {
                if (e.getClick()==ClickType.LEFT) {
                    //t.service = true;
                    sm.arena = t;
                    LocalBuilder.openTrasseEditor(p, sm);
                } else if (e.getClick()==ClickType.DROP) {
                    
                }
            }));
            
        }      
       
        
        entry.add(ClickableItem.of(  ItemUtils.add, e-> {
                    final Trasse t = new Trasse(ApiOstrov.generateId(), "новая трасса", p.getWorld().getName(), new LinkedList<>() );
                    t.creator = p.getName();
                    t.createAt = Timer.getTime();
                    t.disabled = true;
                    t.changed = true;
                    Main.trasses.put(t.id, t);
                    sm.arena = t;
                    LocalBuilder.openTrasseEditor(p, sm);
                }
            )
        );        
        
        
        
        pagination.setItems(entry.toArray(new ClickableItem[entry.size()]));
        pagination.setItemsPerPage(45);    


        if (!pagination.isLast()) {
            content.set(0, 8, ClickableItem.of(ItemUtils.nextPage, e 
                    -> {
                content.getHost().open(p, pagination.next().getPage()) ;
            }
            ));
        }
                
        content.set( 0, 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR)
                .name("Закончить режим Билдера")
                .build(), e -> {
                    for (Trasse t:Main.trasses.values()) {
                        if (t.changed) {
                            p.sendMessage("§6Внимание! Трасса "+t.displayName+" §cне сохранена!");
                        }
                    }
                    p.performCommand("builder end");
                    Main.lobbyPlayer(p);
            }
        ));
        
        
        if (!pagination.isFirst()) {
            content.set(0, 0, ClickableItem.of(ItemUtils.previosPage, e 
                    -> {
                content.getHost().open(p, pagination.previous().getPage()) ;
               })
            );
        }

        pagination.addToIterator(content.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(1, 0)).allowOverride(false));
        


        
        
        
    }
    
    
    
    
    
    

    
    
    
    
    
    
    
}