package net.minecraft.world;

import it.unimi.dsi.fastutil.ints.IntBigList;
import net.minecraft.entity.SpawnGroup;

public class SpawnHelper {
    public record Info() {
        public Info getGroupToCount() {
            return this;
        }

        public int getInt(SpawnGroup spawnGroup) {
            return 0;
        }

        public int getSpawningChunkCount() {
            return 0;
        }

        public int[] values() {
            return new int[0];
        }
    }
}
