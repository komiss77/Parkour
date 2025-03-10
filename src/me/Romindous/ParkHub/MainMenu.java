package me.Romindous.ParkHub;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import net.kyori.adventure.text.Component;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.*;
import ru.komiss77.utils.TCUtil;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.utils.inventory.Pagination;
import ru.komiss77.utils.inventory.SlotIterator;
import ru.komiss77.utils.inventory.SlotPos;




public class MainMenu implements InventoryProvider {
    
    //private static final ClickableItem fill = ClickableItem.empty(new ItemBuilder(Material.WHITE_STAINED_GLASS_PANE).name("§8.").build());
    private static final ItemStack random;
    
    static {
        random = new ItemBuilder(Material.SPYGLASS)
                .name("§3Рандомная Карта")
                .lore("§7Нажмите для выбора")
                .lore("§3рандомной §7карты!")
                .build()
                ;
    }
    
    
    
    @Override
    public void init(final Player p, final InventoryContent content) {
        //p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        p.playSound(p.getEyeLocation(), Sound.BLOCK_BEEHIVE_ENTER, 1, 1);
        
        final Pagination pagination = content.pagination();
        final ArrayList<ClickableItem> entry = new ArrayList<>();        

//final long l = System.currentTimeMillis();

        final PD pd = Main.data.get(p.getName());
        
        
        content.set(2, ClickableItem.of(new ItemBuilder(pd.hideCompleted ? Material.LIGHTNING_ROD : Material.LEVER)
                .name("§3Фильтр по прохождению")
                .lore( "§7Сейчас показаны: §f"+(pd.hideCompleted ? "непройденные" : "все") )
                .lore("§7Клик - показать §b" + (pd.hideCompleted ? "все" : "непройденные") )
                .build(), e-> {
                    pd.hideCompleted = !pd.hideCompleted;
                    reopen(p, content);
                }));


        content.set(4, ClickableItem.of(random, e-> {
                    final List <Trasse> candidate = new ArrayList<>();
                    for (Trasse t : Main.trasses.values()) {
                        if (!t.disabled  && !pd.completions.containsKey(t.id) ) {//!t.isCompleted(pd)) {
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
        
        
        
        
        
        Progress progress;
        CompleteInfo ci;
        
        for (Trasse trasse : Main.trasses.values()) {
            
            if (trasse.disabled) continue;
            if (pd.showLevel!=null && pd.showLevel!=trasse.level) continue;
            ci = pd.completions.get(trasse.id);
            if (pd.hideCompleted && ci != null) continue;
            progress = pd.progress.get(trasse.id);
            
            final List<Component> lore = new ArrayList<>();
                lore.add(Component.text(trasse.inProgress.isEmpty() ? "§7никого нет" : ("§7Проходят: "+trasse.inProgress.size()) ) );
                lore.add(Component.text("§6----------------------") );
                lore.add(Component.text("§7Сложность: §5"+trasse.level.name()) );
                lore.add(Component.text("§7Создан: §3"+TimeUtil.ddMMyy(trasse.createAt)) );
                lore.add(Component.text("§7Создатель: §f"+trasse.creator) );
                lore.add(Component.text("§7⚐: "+trasse.size()) );
                lore.add(Component.text("§7Пройден: "+trasse.totalDone+" раз.") );
                //Component.text("§7⌚ "+TimeUtil.secondToTime(trasse.totalTime)),
                lore.add(Component.text("§7⇪: "+trasse.totalFalls) );
                lore.add(Component.text("§7☠: "+trasse.totalJumps) );
                lore.add(Component.text(trasse.descr) );
                lore.add(Component.text("§6----------------------"));
                //Component.text(t.isCompleted(pd) ? "§fПройден §a"+progress.done+" §fраз" : (t.hasProgress(pd) ? "§7Ваш прогресс:": "§fНе начат")),
                lore.add(Component.text( ci!=null ? "§fВаши прохождения: §a"+ci.done : "§7Не пройден ни разу.") );
                //Component.text( (trasse.isCompleted(pd) || !trasse.hasProgress(pd)) ? "" : StringUtil.getPercentBar(trasse.size()-1, progress.checkPoint, true) ),
                //Component.text(trasse.hasProgress(pd) ? "§7⌚"+TimeUtil.secondToTime(progress.trasseTime) : ""),
                //Component.text(trasse.hasProgress(pd) ? "⚐:"+progress.checkPoint+" ⇪:"+progress.trasseJump+" ☠:"+progress.trasseFalls : ""),
                //Component.text("§6----------------------"),
                //Component.text("§7ЛКМ - "+ (trasse.isCompleted(pd) ? "§cПройти заново" : (trasse.hasProgress(pd) ? "§aПродолжить" : "§bНачать") ) ),
                //Component.text("§7ПКМ - ТОП ")
            
            if (progress == null) {
                //lore.add( Component.text( "§8не начат") );
                lore.add( Component.text( "§6----------------------") );
                if (ci!=null) {
                    lore.add( Component.text( "§7ЛКМ - §bПройти заново") );
                    lore.add( Component.text( "§e*§6Предыдущий результат") );
                    lore.add( Component.text( "§e*§6будет перезаписан!") );
                } else {
                    lore.add( Component.text( "§7ЛКМ - §bНачать прохождение") );
                }
                
            } else if (progress.haProgress()) {
                lore.add(Component.text("§7Текущий прогресс"));
                lore.add( Component.text( StringUtil.getPercentBar(trasse.size()-1, progress.checkPoint, true) ) );
                lore.add(Component.text( "§7⌚"+TimeUtil.secondToTime(progress.trasseTime) ) );
                lore.add(Component.text( "⚐:"+progress.checkPoint+" ⇪:"+progress.trasseJump+" ☠:"+progress.trasseFalls ) );
                lore.add( Component.text( "§6----------------------") );
                lore.add( Component.text( "§7ЛКМ - §aПродолжить") );
            } else {
                lore.add( Component.text( "§8нет прогресса") );
                lore.add( Component.text( "§6----------------------") );
                lore.add( Component.text( "§7ЛКМ - §aПродолжить прохождение") );
            }
            lore.add( Component.text( "§7ПКМ - ТОП") );
            
            
            final ItemStack is = new ItemStack(trasse.mat);
            final ItemMeta im = is.getItemMeta();
            im.displayName(TCUtil.form(trasse.displayName));
            im.lore(lore);
            is.setItemMeta(im);
            
            entry.add(ClickableItem.of(is, e-> {
                
                if (e.isLeftClick()) {
                    
                    /*if (trasse.isCompleted(pd)) {
                        
                        ConfirmationGUI.open(p, "§4Сбросить и пройти заново?", (confirm)-> {
                            
                            if (confirm) {
                                Main.joinParkur(p, trasse.id);
                            } else {
                                reopen(p, content);
                            }
                            
                        });
                        
                        
                    } else {*/
                        
                        Main.joinParkur(p, trasse.id);
                        
                   // }
                    
                } else if (e.isRightClick()) {
                    
                    Main.openTop(p, trasse);
                
                }
                
                
            }));
        }
        
        
        
        

        
        

        pagination.setItems(entry.toArray(ClickableItem[]::new));
        pagination.setItemsPerPage(45);    


        if (!pagination.isLast()) {
            content.set(0, 8, ClickableItem.of(ItemUtil.nextPage, e 
                    -> {
                content.getHost().open(p, pagination.next().getPage()) ;
            }
            ));
        }

        if (!pagination.isFirst()) {
            content.set(0, 0, ClickableItem.of(ItemUtil.previosPage, e 
                    -> {
                content.getHost().open(p, pagination.previous().getPage()) ;
               })
            );
        }

        pagination.addToIterator(content.newIterator(SlotIterator.Type.HORIZONTAL, SlotPos.of(1, 0)).allowOverride(false));


//Ostrov.log_warn("Меню создано за "+(System.currentTimeMillis()-l)+"мс.");
        

    }
    
    
    
    
    
    
    
    @Override
    public void onClose(final Player p, final InventoryContent content) {
        PM.getOplayer(p).menu.current = null;
    }

    
    
    
}
