package me.Romindous.ParkHub.builder;

import me.Romindous.ParkHub.CheckPoint;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import ru.komiss77.builder.SetupMode;
import ru.komiss77.modules.player.PM;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.ClickableItem;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;
import ru.komiss77.ApiOstrov;
import ru.komiss77.utils.inventory.InputButton;
import me.Romindous.ParkHub.Trasse;










public class PointSettings implements InventoryProvider{

    private final SetupMode sm;
    private final CheckPoint cp;
    
    
    PointSettings(final SetupMode sm, final CheckPoint cp) {
        this.sm = sm;
        this.cp = cp;
    }
        
    
    
    @Override
    public void init(final Player p, final InventoryContent content) {
        
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        
        
        final Trasse t = (Trasse) sm.arena;
        


        
        content.set(0, new InputButton(InputButton.InputType.ANVILL, new ItemBuilder(Material.OAK_SIGN)
                .name("§7Назначить номер чекпоинта")

                .lore("§7")
                .lore("§7Сейчас: §а"+t.points.indexOf(cp))
                .lore("§7")
                .lore("§7ЛКМ - изменить")
                .lore("§7")
                .build(),  ""+t.points.indexOf(cp), msg -> {

                    final int amm = ApiOstrov.getInteger(msg);
                    
                    if (amm == Integer.MIN_VALUE) {
                        p.sendMessage("§cДолжно быть число!");
                        PM.soundDeny(p);
                        return;
                    }
                    if (amm<0 || amm>(t.points.size()-1) ) {
                        p.sendMessage("§cот 0 до "+(t.points.size()-1));
                        PM.soundDeny(p);
                        return;
                    }
                    t.points.remove(cp);
                    t.points.add(amm, cp);
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.5f, 1);
                    t.changed = true;
                    reopen(p, content);
                }));  



        
        
        content.set(1, new InputButton(InputButton.InputType.ANVILL, new ItemBuilder(Material.CLOCK)
                .name("§7Контрольное время")
                .lore("§7для достижения этого")
                .lore("§7чекпоинта от предыдущего.")
                .lore("§7Если пройти быстрее,")
                .lore("§7игрок получит метку §cчит")
                .lore("§7")
                .lore("§7Сейчас: "+ApiOstrov.secondToTime(cp.controlTime))
                .lore("§7")
                .lore("§7Устанавливается в СЕКУНДАХ")
                .lore("§7ЛКМ - изменить")
                .lore("§7")
                .build(),  ""+cp.controlTime, msg -> {

                    final int amm = ApiOstrov.getInteger(msg);
                    
                    if (amm == Integer.MIN_VALUE) {
                        p.sendMessage("§cДолжно быть число!");
                        PM.soundDeny(p);
                        return;
                    }
                    if (amm<1 || amm>600) {
                        p.sendMessage("§cот 1 до 600 секунд");
                        PM.soundDeny(p);
                        return;
                    }
                    cp.controlTime = amm;
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.5f, 1);
                    t.changed = true;
                    reopen(p, content);
                }));          
        
        
        
        
        
        content.set(2, new InputButton(InputButton.InputType.ANVILL, new ItemBuilder(Material.SLIME_BLOCK)
                .name("§7Контрольные прыжки")
                .lore("§7для достижения этого")
                .lore("§7чекпоинта от предыдущего.")
                .lore("§7Если будет меньше,")
                .lore("§7игрок получит метку §cчит")
                .lore("§7")
                .lore("§7Сейчас: "+cp.controlJump)
                .lore("§7")
                .lore("§7ЛКМ - изменить")
                .lore("§7")
                .build(),  ""+cp.controlJump, msg -> {

                    final int amm = ApiOstrov.getInteger(msg);
                    
                    if (amm == Integer.MIN_VALUE) {
                        p.sendMessage("§cДолжно быть число!");
                        PM.soundDeny(p);
                        return;
                    }
                    if (amm<1 || amm>100) {
                        p.sendMessage("§cот 1 до 100 секунд");
                        PM.soundDeny(p);
                        return;
                    }
                    cp.controlJump = amm;
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.5f, 1);
                    t.changed = true;
                    reopen(p, content);
                }));          
        
                
        
        
        content.set(3, new InputButton(InputButton.InputType.ANVILL, new ItemBuilder(Material.BONE)
                .name("§7Контрольные падения")
                .lore("§7для достижения этого")
                .lore("§7чекпоинта от предыдущего.")
                .lore("§7Если будет меньше,")
                .lore("§7игрок получит метку §cчит")
                .lore("§7")
                .lore("§7Сейчас: "+cp.controlFall)
                .lore("§7")
                .lore("§7ЛКМ - изменить")
                .lore("§7")
                .build(),  ""+cp.controlFall, msg -> {

                    final int amm = ApiOstrov.getInteger(msg);
                    
                    if (amm == Integer.MIN_VALUE) {
                        p.sendMessage("§cДолжно быть число!");
                        PM.soundDeny(p);
                        return;
                    }
                    if (amm<1 || amm>100) {
                        p.sendMessage("§cот 1 до 100 секунд");
                        PM.soundDeny(p);
                        return;
                    }
                    cp.controlFall = amm;
                    p.playSound(p.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 0.5f, 1);
                    t.changed = true;
                    reopen(p, content);
                }));          
                
        
        

    
            
                
        content.set( 4, ClickableItem.of( new ItemBuilder(Material.OAK_DOOR)
                .name("назад")
                .build(), e -> {
                    LocalBuilder.openCheckPointEditor(p, sm);
            }
        ));
                 

        


        
        

 
        
    
    
    
    }
    

    
    

    
    

    
    
    
    
    
    
    
}