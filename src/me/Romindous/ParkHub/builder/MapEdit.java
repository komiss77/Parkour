package me.Romindous.ParkHub.builder;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import ru.komiss77.builder.SetupMode;
import ru.komiss77.utils.ItemBuilder;
import ru.komiss77.utils.inventory.InventoryContent;
import ru.komiss77.utils.inventory.InventoryProvider;










public class MapEdit implements InventoryProvider{

    private static final ItemStack fill = new ItemBuilder(Material.BLACK_STAINED_GLASS_PANE).build();;
    private final SetupMode sm;
    
    
    MapEdit(final SetupMode sm) {
        this.sm = sm;
    }
        
    
    
    @Override
    public void init(final Player p, final InventoryContent content) {
        
        p.playSound(p.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 5, 5);
        

        
        
        
        
        
        
        
        
            
            
            
      /*      
                if (arena.cuboid.loc1!=null && arena.cuboid.loc2!=null) {
                    contents.set(1, 1, ClickableItem.of( new ItemBuilder(Material.HOPPER)
                        .name("§7Скан кубоида")
                        .lore("§c")
                        .lore("§cВнимание!")
                        .lore("§cДанные о расположении сундуков")
                        .lore("§cбудут обновлены данными")
                        .lore("§cс текущей карты.")
                        .lore("§c")
                        .lore("§eНе забудьте сохранить данные,")
                        .lore("§eили труды будут напраcны!")
                        .build(), e -> {
                            if (arena.state==Enums.ArenaState.РЕГЕНЕРАЦИЯ) {
                                player.sendMessage("§cНа арене идёт регенерация!");
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 5);
                            } else if (arena.state==Enums.ArenaState.СКАНИРОВАНИЕ ) {
                                player.sendMessage("§cСканирование уже запущено для этой арены !");
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 5);
                            } else {
                                player.playSound(player.getLocation(), Sound.BLOCK_COMPARATOR_CLICK, 1, 5);
                                ConfirmationGUI.open( player, "§4Начать сканирование ?", result -> {
                                    if (result) {
                                        arena.scanTask = new ArenaScanBlocksTask(SG.plugin, player, arena);
                                        player.sendMessage("§6Cканирование начато.");
                                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                                    } else {
                                        reopen(player, contents);
                                        player.playSound(player.getLocation(), Sound.ENTITY_LEASH_KNOT_PLACE, 0.5f, 0.85f);
                                    }
                                });
                            }
                            //reopen(player, contents);
                        }));
                }
                
                
        contents.set( 1, 2, ClickableItem.of( new ItemBuilder(Material.COMPOSTER)
                .name("редактор категорий")
                .lore("Категории общие для всех арен!")
                .build(), e -> 
        {
            MapbuilderListener.openChestContentManager(player,arena);
        }
        ));
                 
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        
        if (arena.enabled) {
                 contents.set(1, 4, ClickableItem.of( new ItemBuilder(Material.GREEN_CONCRETE)
                        .name("§7блокировка арены")
                        .lore("§7сейчас §2разблокирована")
                        .build(), e -> {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                            arena.enabled=false;
                            reopen(player, contents);
                        }));
            } else {
                contents.set(1, 4, ClickableItem.of( new ItemBuilder(Material.RED_CONCRETE)
                        .name("§7блокировка арены")
                    .lore("§7сейчас §4заблокирована")
                    .lore("§7(игра на арене невозможна)")
                    .build(), e -> {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.enabled=true;
                        reopen(player, contents);
                    }));
            }
            
        
        
        
        
        
            if (arena.spectatorsLocation==null) {
                 contents.set(2, 4, ClickableItem.of( new ItemBuilder(Material.BARRIER)
                        .name("§cточка ТП зрителя.")
                        .lore("§7Клик - установить.")
                        .build(), e -> {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                            arena.spectatorsLocation=new CLocation(player.getLocation());
                            //arena.spectatorsLocation.setYaw(player.getLocation().getYaw());
                            //arena.spectatorsLocation.setPitch(player.getLocation().getPitch());
                            reopen(player, contents);
                        }));
            } else {
                contents.set(2, 4, ClickableItem.of( new ItemBuilder(Material.PLAYER_HEAD)
                    .name("§6точка ТП зрителя.")
                    .lore("§7ЛКМ-тп")
                    .lore("§7ПКМ-установить")
                    .build(), e -> {
                        if (e.isLeftClick()) {
                            player.teleport(arena.spectatorsLocation.getLocation(arena.worldName));
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1, 5);
                        } else if (e.isRightClick()) {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                            arena.spectatorsLocation=new CLocation(player.getLocation());
                            //arena.spectatorsLocation.setYaw(player.getLocation().getYaw());
                            //arena.spectatorsLocation.setPitch(player.getLocation().getPitch());
                            reopen(player, contents);
                        }
                    }));
            }
            
            if (arena.cuboid.loc2==null) {
                 contents.set(1, 7, ClickableItem.of( new ItemBuilder(Material.BARRIER)
                    .name("§7верхняя точка кубоида.")
                    .lore("§7")
                    .lore("§7Функция кубоида:")
                    .lore("§7- игроки не смогут покинуть кубоид.")
                    .lore("§7- содержимое сундуков обновляется")
                    .lore("§7только в кубоиде.")
                    .lore("§7- игроки не могут ставить/ломать блоки")
                    .lore("§7вне кубоида")
                    .lore("§7")
                    .lore("§7Клик - установить.")
                    .build(), e -> {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.cuboid.setLoc2(player.getLocation());
                        player.performCommand("/pos2");
                        reopen(player, contents);
                    }));
            } else {
                player.sendBlockChange(arena.cuboid.loc2, Material.EMERALD_BLOCK.createBlockData());
                contents.set(1, 7, ClickableItem.of( new ItemBuilder(Material.OAK_FENCE)
                    .name("§7верхняя точка кубоида.")
                    .lore("§7")
                    .lore("§7Функция кубоида:")
                    .lore("§7- игроки не смогут покинуть кубоид.")
                    .lore("§7- содержимое сундуков обновляется")
                    .lore("§7только в кубоиде.")
                    .lore("§7- игроки не могут ставить/ломать блоки")
                    .lore("§7вне кубоида")
                    .lore("§7")
                    .lore("§7ЛКМ-тп")
                    .lore("§7ПКМ-установить")
                    .build(), e -> {
                        if (e.isLeftClick()) {
                            player.teleport(arena.cuboid.loc2);
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1, 5);
                        } else if (e.isRightClick()) {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                            arena.cuboid.setLoc2(player.getLocation());
                            player.performCommand("/pos2");
                            reopen(player, contents);
                        }
                    }));
            }
            

            
            
            
            
            
            
            
            
            
            
            
        
            contents.set(2, 1, ClickableItem.of( new ItemBuilder(Material.EXPERIENCE_BOTTLE)
                    .name("§7редактор перманетных зелий")
                    .lore("§7Действуют со старта игры")
                    .lore("§7ЛКМ-открыть редактор")
                    .build(), e -> {
                        MapbuilderListener.openEffectEditorMenu(player, arena);
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                    }));
            
        
            contents.set(2, 3, ClickableItem.of( new ItemBuilder(Material.WHITE_BED)
                    .name("§7редактор точек спавна")
                    .lore("§7ЛКМ-открыть редактор")
                    .build(), e -> {
                        MapbuilderListener.openSpawnEditorMenu(player, arena);
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                    }));

            contents.set(2, 5, ClickableItem.of( new ItemBuilder(Material.CHEST)
                    .name("§7редактор содержимого сундуков")
                    .lore("§7ЛКМ-открыть редактор")
                    .build(), e -> {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        MapbuilderListener.openChestEditorMenu(player, arena);
                    }));
        
            
            contents.set(2, 7, ClickableItem.of( new ItemBuilder(Material.BREAD)
                    .name("§7редактор спонсорства")
                    .lore("")
                    .lore("§7Зрители могут спонсировать игроков")
                    .lore("")
                    .lore("§7ЛКМ-открыть редактор")
                    .build(), e -> {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        MapbuilderListener.openSponsorEditorMenu(player, arena);
                    }));
        
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
            
        
        
            if (arena.cuboid.loc1==null) {
                 contents.set(3, 1, ClickableItem.of( new ItemBuilder(Material.BARRIER)
                    .name("§7нижняя точка кубоида.")
                    .lore("§7")
                    .lore("§7Функция кубоида:")
                    .lore("§7- игроки не смогут покинуть кубоид.")
                    .lore("§7- содержимое сундуков обновляется")
                    .lore("§7только в кубоиде.")
                    .lore("§7- игроки не могут ставить/ломать блоки")
                    .lore("§7вне кубоида")
                    .lore("§7")
                    .lore("§7Клик - установить.")
                    .build(), e -> {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.cuboid.setLoc1(player.getLocation());
                        player.performCommand("/pos1");
                        reopen(player, contents);
                    }));
            } else {
                player.sendBlockChange(arena.cuboid.loc1, Material.EMERALD_BLOCK.createBlockData());
                contents.set(3, 1, ClickableItem.of( new ItemBuilder(Material.OAK_FENCE)
                    .name("§7нижняя точка кубоида.")
                    .lore("§7")
                    .lore("§7Функция кубоида:")
                    .lore("§7- игроки не смогут покинуть кубоид.")
                    .lore("§7- содержимое сундуков обновляется")
                    .lore("§7только в кубоиде.")
                    .lore("§7- игроки не могут ставить/ломать блоки")
                    .lore("§7вне кубоида")
                    .lore("§7")
                    .lore("§7ЛКМ-тп")
                    .lore("§7ПКМ-установить")
                    .build(), e -> {
                        if (e.isLeftClick()) {
                            player.teleport(arena.cuboid.loc1);
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1, 5);
                        } else if (e.isRightClick()) {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                            arena.cuboid.setLoc1(player.getLocation());
                            player.performCommand("/pos1");
                            reopen(player, contents);
                        }
                    }));
            }
            

            
            

            if (arena.deadMatcLocation==null) {
                 contents.set(3, 4, ClickableItem.of( new ItemBuilder(Material.BARRIER)
                    .name("§cточка деадматча (бойцовая яма)")
                    .lore("")
                    .lore("§7Когда выйдет время игры,")
                    .lore("§7Выжившие будут телепортированы")
                    .lore("§7в эту зону с разбросом +/- 2 блока")
                    .lore("§7для поединка.")
                    .lore("§7Для лучшего эффекта, рекомендуется")
                    .lore("§7поставить эту точку выше на 40-50 блоков.")
                    .lore("§7Проверьте отсутствие препятствий")
                    .lore("§7на пути падения!")
                    .lore("§7Урона при падении не будет.")
                    .lore("")
                    .lore("§7Клик - установить.")
                    .build(), e -> {
                        if (AM.checkSurroundLocations(player.getLocation(),2)) {
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                            arena.deadMatcLocation=new CLocation(player.getLocation());
                            //arena.deadMatcLocation.setYaw(player.getLocation().getYaw());
                            //arena.deadMatcLocation.setPitch(player.getLocation().getPitch());
                            reopen(player, contents);
                        } else {
                            player.sendMessage("§cНе должно быть ничего ближе 2 блоков!");
                        }
                    }));
            } else {
                contents.set(3, 4, ClickableItem.of( new ItemBuilder(Material.WOODEN_SWORD)
                    .name("§cточка деадматча (бойцовая яма)")
                    .lore("")
                    .lore("§7Когда выйдет время игры,")
                    .lore("§7Выжившие будут телепортированы")
                    .lore("§7в эту зону с разбросом +/- 2 блока")
                    .lore("§7для поединка.")
                    .lore("§7Для лучшего эффекта, рекомендуется")
                    .lore("§7поставить эту точку выше на 40-50 блоков.")
                    .lore("§7Проверьте отсутствие препятствий")
                    .lore("§7на пути падения!")
                    .lore("§7Урона при падении не будет.")
                    .lore("")
                    .lore("§7ЛКМ-тп")
                    .lore("§7ПКМ-установить")
                    .build(), e -> {
                        if (e.isLeftClick()) {
                            player.teleport(arena.deadMatcLocation.getLocation(arena.worldName));
                            player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_IRON_XYLOPHONE, 1, 5);
                        } else if (e.isRightClick()) {
                            if (AM.checkSurroundLocations(player.getLocation(),2)) {
                                player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                                arena.deadMatcLocation=new CLocation(player.getLocation());
                                //arena.deadMatcLocation.setYaw(player.getLocation().getYaw());
                                //arena.deadMatcLocation.setPitch(player.getLocation().getPitch());
                                reopen(player, contents);
                            } else {
                                player.sendMessage("§cНе должно быть ничего ближе 2 блоков!");
                            }
                        }
                    }));
            }
            

      


            
            
            contents.set(3, 6, ClickableItem.of( new ItemBuilder(Material.CARTOGRAPHY_TABLE)
                    .name("§7таймер наполнения сундуков")
                    .lore("§7ЛКМ-открыть редактор")
                    .build(), e -> {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        MapbuilderListener.openRefillTimeEditorMenu(player, arena);
                    }));
        
            
            
            contents.set(3, 7, ClickableItem.of( new ItemBuilder(Material.FLETCHING_TABLE)
                    .name("§7таймер генерации Даров Смерти")
                    .lore("§7ЛКМ - открыть редактор")
                    .lore("")
                    .lore("§bПКМ - менять категорию Даров")
                    .lore("§fТекущая категория: "+arena.deathlyHallowsCategory)
                    .lore("")
                    .build(), e -> {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        if (e.isLeftClick()) {
                            MapbuilderListener.openDeadlyHallowsTimeEditorMenu(player, arena);
                        } else if (e.isRightClick()) {
                            arena.deathlyHallowsCategory=ChestContentManager.switchCategory(arena.deathlyHallowsCategory);
                            reopen(player, contents);
                        }
                    }));
        
            
            
            
            
            
            
            
            
       
                
                
                
               
            
            
        
        


        
        
        
        
        
        
        
        
        
        
        
       
        
       if (arena.lobbyTime>5 && arena.lobbyTime<180) {
            contents.set(4, 1, ClickableItem.of( new ItemBuilder(Material.PURPLE_DYE)
                .name("§2время до старта")
                .lore("")
                .lore("§7Сейчас §6"+arena.lobbyTime+" сек.")
                .lore("")
                .lore("ЛКМ +1 секунда")
                .lore("ПКМ -1 секунда")
                .lore("")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.lobbyTime+=1;
                        reopen(player, contents);
                    } else if (e.isRightClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.lobbyTime-=1;
                        reopen(player, contents);
                    }
                }));
        } else if (arena.lobbyTime>=180) {
            contents.set(4, 1, ClickableItem.of( new ItemBuilder(Material.RED_DYE)
                .name("§4время до старта")
                .lore("")
                .lore("§7Сейчас §6"+arena.lobbyTime+" сек.")
                .lore("")
                .lore("§cВерхний лимит!")
                .lore("ЛКМ -1 секунда")
                .lore("")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 5);
                        reopen(player, contents);
                    } else if (e.isRightClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.lobbyTime-=1;
                        reopen(player, contents);
                    }
                }));
        } else if (arena.lobbyTime<=5) {
            contents.set(4, 1, ClickableItem.of( new ItemBuilder(Material.RED_DYE)
                .name("§4время до старта")
                .lore("")
                .lore("§7Сейчас §6"+arena.lobbyTime+" сек.")
                .lore("")
                .lore("§cНижний лимит!")
                .lore("ЛКМ +1 секунда")
                .lore("")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.lobbyTime+=1;
                        reopen(player, contents);
                    } else if (e.isRightClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 5);
                        reopen(player, contents);
                    }
                }));
        }

        

       
       
       
       if (arena.equipTime>5 && arena.equipTime<60) {
            contents.set(4, 2, ClickableItem.of( new ItemBuilder(Material.PURPLE_DYE)
                .name("§2время сбора аммуниции")
                .lore("")
                .lore("§7Сейчас §6"+arena.equipTime+" сек.")
                .lore("")
                .lore("ЛКМ +1 секунда")
                .lore("ПКМ -1 секунда")
                .lore("")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.equipTime+=1;
                        reopen(player, contents);
                    } else if (e.isRightClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.equipTime-=1;
                        reopen(player, contents);
                    }
                }));
        } else if (arena.equipTime>=60) {
            contents.set(4, 2, ClickableItem.of( new ItemBuilder(Material.RED_DYE)
                .name("§4время сбора аммуниции")
                .lore("")
                .lore("§7Сейчас §6"+arena.equipTime+" сек.")
                .lore("")
                .lore("§cВерхний лимит!")
                .lore("ЛКМ -1 секунда")
                .lore("")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 5);
                        reopen(player, contents);
                    } else if (e.isRightClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.equipTime-=1;
                        reopen(player, contents);
                    }
                }));
        } else if (arena.equipTime<=5) {
            contents.set(4, 2, ClickableItem.of( new ItemBuilder(Material.RED_DYE)
                .name("§4время сбора аммуниции")
                .lore("")
                .lore("§7Сейчас §6"+arena.equipTime+" сек.")
                .lore("")
                .lore("§cНижний лимит!")
                .lore("ЛКМ +1 секунда")
                .lore("")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.equipTime+=1;
                        reopen(player, contents);
                    } else if (e.isRightClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 5);
                        reopen(player, contents);
                    }
                }));
        }

        

        
        
        if (arena.gameLength>120 && arena.gameLength<7200) {
            contents.set(4, 3, ClickableItem.of( new ItemBuilder(Material.PURPLE_DYE)
                .name("§2время игры")
                .lore("")
                .lore("§7Сейчас §6"+ApiOstrov.secondToTime(arena.gameLength))
                .lore("")
                .lore("ЛКМ +1 минута")
                .lore("ПКМ -1 минута")
                .lore("")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.gameLength+=60;
                        reopen(player, contents);
                    } else if (e.isRightClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.gameLength-=60;
                        reopen(player, contents);
                    }
                }));
        } else if (arena.gameLength>=7200) {
            contents.set(4, 3, ClickableItem.of( new ItemBuilder(Material.RED_DYE)
                .name("§4время игры")
                .lore("")
                .lore("§7Сейчас §6"+ApiOstrov.secondToTime(arena.gameLength))
                .lore("")
                .lore("§cВерхний лимит!")
                .lore("ПКМ -1 минута")
                .lore("")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 5);
                        reopen(player, contents);
                    } else if (e.isRightClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.gameLength-=60;
                        reopen(player, contents);
                    }
                }));
        } else if (arena.gameLength<=120) {
            contents.set(4, 3, ClickableItem.of( new ItemBuilder(Material.RED_DYE)
                .name("§4время игры")
                .lore("")
                .lore("§7Сейчас §6"+ApiOstrov.secondToTime(arena.gameLength))
                .lore("")
                .lore("§cНижний лимит!")
                .lore("ЛКМ +1 минута")
                .lore("")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.gameLength+=60;
                        reopen(player, contents);
                    } else if (e.isRightClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 5);
                        reopen(player, contents);
                    }
                }));
        }

        
        
        

       
       
       
       if (arena.deadMatchEquipTime>5 && arena.deadMatchEquipTime<60) {
            contents.set(4, 4, ClickableItem.of( new ItemBuilder(Material.PURPLE_DYE)
                .name("§2подготовка к поединку")
                .lore("")
                .lore("§7Сейчас §6"+arena.deadMatchEquipTime+" сек.")
                .lore("")
                .lore("ЛКМ +1 секунда")
                .lore("ПКМ -1 секунда")
                .lore("")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.deadMatchEquipTime+=1;
                        reopen(player, contents);
                    } else if (e.isRightClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.deadMatchEquipTime-=1;
                        reopen(player, contents);
                    }
                }));
        } else if (arena.deadMatchEquipTime>=60) {
            contents.set(4, 4, ClickableItem.of( new ItemBuilder(Material.RED_DYE)
                .name("§4подготовка к поединку")
                .lore("")
                .lore("§7Сейчас §6"+arena.deadMatchEquipTime+" сек.")
                .lore("")
                .lore("§cВерхний лимит!")
                .lore("ЛКМ -1 секунда")
                .lore("")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 5);
                        reopen(player, contents);
                    } else if (e.isRightClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.deadMatchEquipTime-=1;
                        reopen(player, contents);
                    }
                }));
        } else if (arena.deadMatchEquipTime<=5) {
            contents.set(4, 4, ClickableItem.of( new ItemBuilder(Material.RED_DYE)
                .name("§4подготовка к поединку")
                .lore("")
                .lore("§7Сейчас §6"+arena.deadMatchEquipTime+" сек.")
                .lore("")
                .lore("§cНижний лимит!")
                .lore("ЛКМ +1 секунда")
                .lore("")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.deadMatchEquipTime+=1;
                        reopen(player, contents);
                    } else if (e.isRightClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 5);
                        reopen(player, contents);
                    }
                }));
        }

        

       
       

        
        
        if (arena.deadMatchLength>120 && arena.deadMatchLength<600) {
            contents.set(4, 5, ClickableItem.of( new ItemBuilder(Material.PURPLE_DYE)
                .name("§2время поединка")
                .lore("")
                .lore("§7Сейчас §6"+ApiOstrov.secondToTime(arena.deadMatchLength))
                .lore("")
                .lore("ЛКМ +1 минута")
                .lore("ПКМ -1 минута")
                .lore("")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.deadMatchLength+=60;
                        reopen(player, contents);
                    } else if (e.isRightClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.deadMatchLength-=60;
                        reopen(player, contents);
                    }
                }));
        } else if (arena.deadMatchLength>=600) {
            contents.set(4, 5, ClickableItem.of( new ItemBuilder(Material.RED_DYE)
                .name("§4время поединка")
                .lore("")
                .lore("§7Сейчас §6"+ApiOstrov.secondToTime(arena.deadMatchLength))
                .lore("")
                .lore("§cВерхний лимит!")
                .lore("ПКМ -1 минута")
                .lore("")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 5);
                        reopen(player, contents);
                    } else if (e.isRightClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.deadMatchLength-=60;
                        reopen(player, contents);
                    }
                }));
        } else if (arena.deadMatchLength<=120) {
            contents.set(4, 5, ClickableItem.of( new ItemBuilder(Material.RED_DYE)
                .name("§4время поединка")
                .lore("")
                .lore("§7Сейчас §6"+ApiOstrov.secondToTime(arena.deadMatchLength))
                .lore("")
                .lore("§cНижний лимит!")
                .lore("ЛКМ +1 минута")
                .lore("")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.deadMatchLength+=60;
                        reopen(player, contents);
                    } else if (e.isRightClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 5);
                        reopen(player, contents);
                    }
                }));
        }

        
        
        

       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       if (arena.minTeams>2 && arena.minTeams<arena.maxTeams) {
            contents.set(5, 1, ClickableItem.of( new ItemBuilder(Material.PURPLE_BANNER)
                .name("§2минимум команд/игроков для старта")
                .lore("")
                .lore("§7Сейчас §6"+arena.minTeams)
                .lore("")
                .lore("ЛКМ +1")
                .lore("ПКМ -1")
                .lore("")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.minTeams+=1;
                        reopen(player, contents);
                    } else if (e.isRightClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.minTeams-=1;
                        reopen(player, contents);
                    }
                }));
        } else if (arena.minTeams>=arena.maxTeams) {
            contents.set(5, 1, ClickableItem.of( new ItemBuilder(Material.RED_BANNER)
                .name("§4минимум команд/игроков для старта")
                .lore("")
                .lore("§7Сейчас §6"+arena.minTeams)
                .lore("")
                .lore("§cВерхний лимит! (максимум игроков)")
                .lore("ЛКМ -1")
                .lore("")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 5);
                        reopen(player, contents);
                    } else if (e.isRightClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.minTeams-=1;
                        reopen(player, contents);
                    }
                }));
        } else if (arena.minTeams<=2) {
            contents.set(5, 1, ClickableItem.of( new ItemBuilder(Material.RED_BANNER)
                .name("§4минимум команд/игроков для старта")
                .lore("")
                .lore("§7Сейчас §6"+arena.minTeams)
                .lore("")
                .lore("§cНижний лимит!")
                .lore("ЛКМ +1")
                .lore("")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.minTeams+=1;
                        reopen(player, contents);
                    } else if (e.isRightClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 5);
                        reopen(player, contents);
                    }
                }));
        }

        

        
       
       if (arena.maxTeams>arena.minTeams && arena.maxTeams<arena.teams.size()) {
            contents.set(5, 2, ClickableItem.of( new ItemBuilder(Material.PURPLE_BANNER)
                .name("§2максимум команд/игроков")
                .lore("")
                .lore("§7Сейчас §6"+arena.maxTeams)
                .lore("")
                .lore("ЛКМ +1")
                .lore("ПКМ -1")
                .lore("")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.maxTeams+=1;
                        reopen(player, contents);
                    } else if (e.isRightClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.maxTeams-=1;
                        reopen(player, contents);
                    }
                }));
        } else if (arena.maxTeams>=arena.teams.size()) {
            contents.set(5, 2, ClickableItem.of( new ItemBuilder(Material.RED_BANNER)
                .name("§4максимум команд/игроков")
                .lore("")
                .lore("§7Сейчас §6"+arena.maxTeams)
                .lore("")
                .lore("§cВерхний лимит! (максимум игроков)")
                .lore("ЛКМ -1")
                .lore("")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 5);
                        reopen(player, contents);
                    } else if (e.isRightClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.maxTeams-=1;
                        reopen(player, contents);
                    }
                }));
        } else if (arena.maxTeams<=arena.minTeams) {
            contents.set(5, 2, ClickableItem.of( new ItemBuilder(Material.RED_BANNER)
                .name("§4максимум команд/игроков")
                .lore("")
                .lore("§7Сейчас §6"+arena.maxTeams)
                .lore("")
                .lore("§cНижний лимит!")
                .lore("ЛКМ +1")
                .lore("")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.maxTeams+=1;
                        reopen(player, contents);
                    } else if (e.isRightClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 5);
                        reopen(player, contents);
                    }
                }));
        }

        

       
       
       if (arena.teamSize>1 && arena.teamSize<8) {
            contents.set(5, 3, ClickableItem.of( new ItemBuilder(Material.PURPLE_BANNER)
                .name("§2игроков в коменде")
                .lore("§7При значении 1 будет одиночная игра")
                .lore("")
                .lore("§7Сейчас §6"+arena.teamSize)
                .lore("")
                .lore("ЛКМ +1")
                .lore("ПКМ -1")
                .lore("")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.teamSize+=1;
                        reopen(player, contents);
                    } else if (e.isRightClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.teamSize-=1;
                        reopen(player, contents);
                    }
                }));
        } else if (arena.teamSize>=8) {
            contents.set(5, 3, ClickableItem.of( new ItemBuilder(Material.RED_BANNER)
                .name("§4игроков в коменде")
                .lore("§7При значении 1 будет одиночная игра")
                .lore("")
                .lore("§7Сейчас §6"+arena.teamSize)
                .lore("")
                .lore("§cВерхний лимит! (максимум игроков)")
                .lore("ЛКМ -1")
                .lore("")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 5);
                        reopen(player, contents);
                    } else if (e.isRightClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.teamSize-=1;
                        reopen(player, contents);
                    }
                }));
        } else if (arena.teamSize<=1) {
            contents.set(5, 3, ClickableItem.of( new ItemBuilder(Material.RED_BANNER)
                .name("§4игроков в коменде")
                .lore("§7При значении 1 будет одиночная игра")
                .lore("")
                .lore("§7Сейчас §6"+arena.teamSize)
                .lore("")
                .lore("§cНижний лимит!")
                .lore("ЛКМ +1")
                .lore("")
                .build(), e -> {
                    if (e.isLeftClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BELL, 1, 5);
                        arena.teamSize+=1;
                        reopen(player, contents);
                    } else if (e.isRightClick()) {
                        player.playSound(player.getLocation(), Sound.BLOCK_NOTE_BLOCK_BASEDRUM, 1, 5);
                        reopen(player, contents);
                    }
                }));
        }


       
       
       
       
       
       
       
       

       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
       
      
        
        
        
        
        
        
        
        
        contents.set(4, 8, ClickableItem.of( new ItemBuilder(Material.JUKEBOX)
                .name("§aСохранить текущие данные")
                .lore("§7Промежуточное сохранение")
                .lore("§7данных `как есть`")
                .build(), e -> {
                    saveArena(player,arena);
                    reopen(player, contents);
                }));
        
        
        
        
        final List<String> checkResult = AM.checkArena(arena);
        if (!checkResult.isEmpty()) {
            contents.set(5, 8, ClickableItem.empty(new ItemBuilder(Material.BARRIER)
                .name("§4Арена не готова к работе!")
                .lore(checkResult)
                .build()));
        } else {
            contents.set(5, 8, ClickableItem.of( new ItemBuilder(Material.NETHER_STAR)
                .name("§aСохранить и приготовить к игре")
                .lore("§eВнимание! Так же текущая карта")
                .lore("§eсохранится в регенератор карты!")
                .build(), e -> {
                    if (saveArena(player, arena)) {
                        MapbuilderListener.end(player);
                        player.sendMessage("§aАрена сохранена и готовится к игре!");
                        
                        new BukkitRunnable() {
                            @Override
                            public void run() {
                                arena.resetArena(true);
                            }
                        }.runTaskLater(SG.plugin, 10);
                        
                    } else {
                        player.closeInventory();
                        player.sendMessage("§cСохранение не удалось!");
                    }
            }));
        }
        
        
        
        
        */
 
        
    
    
    
    }
    

    
    

    
    

    
    
    
    
    
    
    
}