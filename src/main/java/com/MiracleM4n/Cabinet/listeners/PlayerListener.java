package com.miraclem4n.cabinet.listeners;

import java.util.ArrayList;

import com.miraclem4n.cabinet.Cabinet;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.craftbukkit.inventory.CraftInventoryDoubleChest;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class PlayerListener implements Listener {
    Cabinet plugin;

    public PlayerListener(Cabinet instance) {
        plugin = instance;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Action action = event.getAction();

        Boolean nearChest = false;

        if (action == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();

            Integer type = block.getTypeId();

            if (type == Material.CHEST.getId()) {
                if (event.useInteractedBlock() == Event.Result.DENY)
                    return;

                if (!(block.getState() instanceof Chest))
                    return;

                Chest chest1 = (Chest) block.getState();
                Chest chest2 = getAdjacentChestBlock(block);

                if (isCovered(block) || !plugin.checkPermissions(player, "cabinet.covered"))
                    return;

                if (chest2 != null) {
                    openDoubleChest(chest1, chest2, player);
                } else {
                    openSingleChest(chest1, player);
                }
            }
        }

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && player.getItemInHand().getType().equals(Material.CHEST)) {
            if (event.getClickedBlock().getType() != Material.CHEST) {
                Block block = event.getClickedBlock();
                BlockFace blockface = event.getBlockFace();
                Block airBlock  = block.getRelative(blockface);

                Integer firstSlot = player.getInventory().first(Material.CHEST);
                Integer num = player.getInventory().getItem(firstSlot).getAmount();
                Integer inf = player.getInventory().getItem(firstSlot).getMaxStackSize();

                if (isNextToDoubleChest(airBlock)) {
                    if (plugin.checkPermissions(player, "cabinet.adjchest")) {
                        if (num == 1 && inf > 64) {
                            player.getInventory().getItem(firstSlot).setAmount(0);
                            airBlock.setType(Material.CHEST);
                        } else if (num == 1 && inf <= 64) {
                            player.getInventory().clear(firstSlot);
                            airBlock.setType(Material.CHEST);
                        } else if (num > 1) {
                            player.getInventory().getItem(firstSlot).setAmount(num - 1);
                            airBlock.setType(Material.CHEST);
                        } else {
                            player.getInventory().getItem(firstSlot).setAmount(0);
                            airBlock.setType(Material.CHEST);
                        }
                    } else
                        player.sendMessage(ChatColor.RED + "You Don't Have Permissions To Place Over 2 Adjacent Chests");
                } else if (block.getType() != Material.CHEST)
                    nearChest = airBlock.getRelative(BlockFace.UP).getType() != Material.AIR || airBlock.getRelative(BlockFace.DOWN).getType() == Material.CHEST;

                if (nearChest)
                    if (!plugin.checkPermissions(player, "cabinet.abovechest")) {
                        player.sendMessage(ChatColor.RED + "You Don't Have Permissions To Place Covered Chests");
                        event.setCancelled(true);
                    }
            }
        }
    }

    boolean isNextToDoubleChest(Block block) {
        ArrayList<Block> list = getAdjacentChestBlocks(block);
        ArrayList<Block> list2;

        if (list.size() > 2)
            return false;

        if (list.size() > 0)
            for (Block b : list) {
                list2 = getAdjacentChestBlocks(b);

                if (list2.size() > 0)
                    return true;
            }

        return false;
    }

    ArrayList<Block> getAdjacentChestBlocks(Block block) {
        ArrayList<Block> list = new ArrayList<Block>();

        Block[] bArr = {
                block.getRelative(1, 0, -1),
                block.getRelative(-1, 0, 1),
                block.getRelative(1, 0, 0),
                block.getRelative(-1, 0, 0),
                block.getRelative(0, 0, 1),
                block.getRelative(0, 0, -1)
        };

        for (Integer i = 0; i < 6; i++)
            if (bArr[i].getState() instanceof Chest)
                list.add(bArr[i]);

        return list;
    }

    boolean isCovered(Block block) {
        return isSolidBlock(getBlockAbove(block));
    }

    boolean isTripleChest(Block block) {
        return getRelativeChests(block) > 1;
    }

    boolean isDoubleChest(Block block) {
        return !isTripleChest(block) && getRelativeChests(block) > 0;
    }

    Integer getRelativeChests(Block block) {
        Integer count = 0;

        if (block.getRelative(1, 0, 0).getType() == Material.CHEST)
            count++;

        if (block.getRelative(-1, 0, 0).getType() == Material.CHEST)
            count++;

        if (block.getRelative(0, 0, 1).getType() == Material.CHEST)
            count++;

        if (block.getRelative(0, 0, -1).getType() == Material.CHEST)
            count++;

        return count;
    }

    Block getBlockAbove(Block block) {
        return block.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ());
    }

    boolean isAdjacentToChest(Block block) {
        return getAdjacentChestBlock(block) != null;
    }

    Chest getAdjacentChestBlock(Block block) {
        BlockState[] bRelative = {
                block.getRelative(1, 0, 0).getState(),
                block.getRelative(-1, 0, 0).getState(),
                block.getRelative(0, 0, 1).getState(),
                block.getRelative(0, 0, -1).getState()
        };

        for (Integer i = 0; i < 4; i++)
            if (bRelative[i] instanceof Chest) {
                Chest chest1 = (Chest) bRelative[i];

                if (!isTripleChest(chest1.getBlock()))
                    return (Chest) bRelative[i];
            }

        return null;
    }

    void openSingleChest(Chest chest, Player player) {
        player.openInventory(chest.getInventory());
    }

    void openDoubleChest(Chest chest1, Chest chest2, Player player) {
        CraftInventory cInventory1 = (CraftInventory) chest1.getBlockInventory();
        CraftInventory cInventory2 = (CraftInventory) chest2.getBlockInventory();

        CraftInventoryDoubleChest doubleChest = new CraftInventoryDoubleChest(cInventory1, cInventory2);

        player.openInventory(doubleChest);
    }

    boolean isSolidBlock(Block block) {
        switch (block.getType()) {
            case AIR:
            case TORCH:
            case LADDER:
            case STONE_BUTTON:
            case LEVER:
            case REDSTONE_TORCH_OFF:
            case REDSTONE_TORCH_ON:
            case PAINTING:
            case WALL_SIGN:

                return false;
        }

        return true;
    }
}
