package me.Romindous.ParkHub;

import org.bukkit.Material;


public enum Level {
    
        Легко (Material.CHAINMAIL_HELMET),
        Нормально (Material.IRON_HELMET),
        Трудно (Material.DIAMOND_HELMET),
        Нереально (Material.NETHERITE_HELMET)
        ;
        
        
        public final Material mat;
        
        private Level (final Material mat) {
            this.mat = mat;
        }
        
        public static Level next (final Level curr) {
            if (curr==null) return Легко;
            switch (curr) {
                case Легко: return Нормально;
                case Нормально: return Трудно;
                case Трудно: return Нереально;
            }
            return null;
        }
}
