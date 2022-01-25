package me.Romindous.ParkHub;


import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.ApiOstrov;
import ru.komiss77.modules.player.Oplayer;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.ItemUtils;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;




public class MainMenu implements InventoryProvider {
    
    
    
    private static final ClickableItem fill = ClickableItem.empty(new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).name("§8.").build());

    

    
    
    
    @Override
    public void init(final Player p, final InventoryContent content) {
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        
        final Pagination pagination = content.pagination();
        final ArrayList<ClickableItem> entry = new ArrayList<>();        


        final PD pd = Main.data.get(p.getName());
        
        
        content.set(2, ClickableItem.of(new ItemBuilder(pd.hideCompleted ? Material.LIGHTNING_ROD : Material.LEVER)
                .name("§3Фильтр по прохождению")
                .lore( "§7Сейчас показаны: §f"+(pd.hideCompleted ? "непройденные" : "все") )
                .lore("§7Клик - показать §b" + (pd.hideCompleted ? "все" : "непройденные") )
                .build(), e-> {
                    pd.hideCompleted = !pd.hideCompleted;
                    reopen(p, content);
                }));


        content.set(4, ClickableItem.of(new ItemBuilder(Material.HEART_OF_THE_SEA)
                .name("§3Рандомная Карта")
                .lore("§7Нажмите для выбора")
                .lore("§3рандомной §7карты!")
                .build(), e-> {
                    final List <Trasse> candidate = new ArrayList<>();
                    for (Trasse t : Main.trasses.values()) {
                        if (!t.disabled || !t.isCompleted(pd)) {
                            candidate.add(t);
                        }
                    }
                    if (candidate.isEmpty()) {
                        p.sendMessage("§eНе удалось найти подходящей Вам трассы(");
                    }
                    Collections.shuffle(candidate);
                    Main.joinParkur(p, candidate.get(0).id);
                }));  
        
        content.set(6, ClickableItem.of(new ItemBuilder(pd.showLevel==null ? Material.LIGHTNING_ROD : pd.showLevel.mat)
                .name("§3Фильтр по сложности")
                .lore("§7Сейчас показаны: §f"+ (pd.showLevel==null ? "все" : pd.showLevel.name()) )
                .lore("§7Клик - показать §b" + (Level.next(pd.showLevel)==null ? "Все" : Level.next(pd.showLevel).name()) )
                .build(), e-> {
                    pd.showLevel = Level.next(pd.showLevel);
                    reopen(p, content);
                }));
        
        
        
        
        
        
        for (Trasse t : Main.trasses.values()) {
            
            if (t.disabled) continue;
            if (pd.hideCompleted && t.isCompleted(pd)) continue;
            if (pd.showLevel!=null && pd.showLevel!=t.level) continue;
            
            final ItemStack is = new ItemBuilder(t.mat)
                .name(t.displayName)
                .lore("§6----------------------")
                .lore("§7Создан: §3"+ApiOstrov.dateFromStamp(t.createAt))
                .lore("§7Создатель: §f"+t.creator)
                .lore("§7Сложность: §5"+t.level.name())
                .lore(t.descr)
                //.lore("§6----------------------")
                .lore(t.inProgress.isEmpty() ? "§7никого нет" : ("§7Проходят: "+t.inProgress.size()) )
                .lore("§7Чекпоинты: "+t.size())
                .lore("§7Пройден: "+t.totalDone+" раз.")
                .lore("§7Наиграно: "+ApiOstrov.secondToTime(t.totalTime))
                .lore("§7Напрыгано: "+t.totalJumps)
                .lore("§7Нападано: "+t.totalFalls)
                .lore("§6----------------------")
                .lore( t.isCompleted(pd) ? "§aПройден" : (t.hasProgress(pd) ? "§7Ваш прогресс: §6"+pd.getProgress(t.id).checkPoint+" §7из §e"+t.size() : "§fНе начат") ) 
                .lore("")
                .lore("")
                .lore("§6----------------------")
                .lore("§7ЛКМ - "+ (t.isCompleted(pd) ? "§cПройти заново" : (t.hasProgress(pd) ? "§aПродолжить" : "§bНачать") ) )
                .lore("§7ПКМ - ТОП ")
                .build();
            
            entry.add(ClickableItem.empty(is));
        }
        
        
        
        

        
        

        pagination.setItems(entry.toArray(new ClickableItem[entry.size()]));
        pagination.setItemsPerPage(45);    


        if (!pagination.isLast()) {
            content.set(0, 8, ClickableItem.of(ItemUtils.nextPage, e 
                    -> {
                content.getHost().open(p, pagination.next().getPage()) ;
            }
            ));
        }

        if (!pagination.isFirst()) {
            content.set(0, 0, ClickableItem.of(ItemUtils.previosPage, e 
                    -> {
                content.getHost().open(p, pagination.previous().getPage()) ;
               })
            );
        }

        pagination.addToIterator(content.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(1, 0)).allowOverride(false));


        
        

    }
    
    
    
    
    
    
    
    @Override
    public void onClose(final Player p, final InventoryContent content) {
        PM.getOplayer(p).menu.current = null;
    }

    
    
    
}
