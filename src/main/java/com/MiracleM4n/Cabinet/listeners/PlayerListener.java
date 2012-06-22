package com.miraclem4n.cabinet.listeners;

import com.miraclem4n.cabinet.Cabinet;
import net.minecraft.server.IInventory;
import net.minecraft.server.InventoryLargeChest;

import java.util.ArrayList;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;

public class PlayerListener implements Listener {
    Cabinet plugin;

    PlayerListener(Cabinet plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();

        Action action = event.getAction();

        if (action == Action.RIGHT_CLICK_BLOCK) {
            Block block = event.getClickedBlock();

            Integer type = block.getTypeId();

            if (type == Material.CHEST.getId()) {
                if(event.useInteractedBlock() == Event.Result.DENY) {
                    event.setCancelled(true);

                    return;
                } else {
                    if (((block.getState() instanceof Chest)) && (tripleChest(block))) {
                        if (adjacentToChest(block)) {
                            Block block2 = getAdjacentChestBlock(block);
                            openDoubleChest(block, block2, player);
                        } else
                            openSingleChest(block, player);

                        event.setCancelled(true);
                    }

                    if (plugin.checkPermissions(player, "cabinet.covered", false)) {
                        if (((block.getState() instanceof Chest)) && (covered(block))) {
                            if (adjacentToChest(block)) {
                                Block block2 = getAdjacentChestBlock(block);
                                openDoubleChest(block, block2, player);
                            } else
                                openSingleChest(block, player);

                            event.setCancelled(true);
                        }
                    }
                }
            }
        }

        if (event.getAction().equals(Action.RIGHT_CLICK_BLOCK) && player.getItemInHand().getType().equals(Material.CHEST)) {
            if(event.getClickedBlock().getType() != Material.CHEST) {

                Block block = event.getClickedBlock();
                BlockFace blockface = event.getBlockFace();
                Block airBlock;

                airBlock = block.getRelative(blockface);

                boolean nearChest = false;

                Integer firstSlot = player.getInventory().first(Material.CHEST);
                Integer num = player.getInventory().getItem(firstSlot).getAmount();
                Integer inf = player.getInventory().getItem(firstSlot).getMaxStackSize();

                if(isNextToDoubleChest(airBlock))
                    if (plugin.checkPermissions(player, "cabinet.adjchest", false)) {
                        if(num == 1 && inf > 64) {
                            player.getInventory().getItem(firstSlot).setAmount(0);
                            airBlock.setType(Material.CHEST);
                        }
                        if(num == 1 && inf <= 64) {
                            player.getInventory().clear(firstSlot);
                            airBlock.setType(Material.CHEST);
                        } else if(num > 1) {
                            player.getInventory().getItem(firstSlot).setAmount(num - 1);
                            airBlock.setType(Material.CHEST);
                        } else {
                            player.getInventory().getItem(firstSlot).setAmount(0);
                            airBlock.setType(Material.CHEST);
                        }
                        if(num < 1)
                            return;

                    } else
                        player.sendMessage(ChatColor.RED + "You Don't Have Permissions To Place Over 2 Adjacent Chests");

                else if (block.getType() != Material.CHEST)
                    nearChest = (airBlock.getRelative(BlockFace.UP).getType() != Material.AIR) ||
                            (airBlock.getRelative(BlockFace.DOWN).getType() == Material.CHEST);

                if (nearChest)
                    if (!plugin.checkPermissions(player, "cabinet.abovechest", false)) {
                        event.setCancelled(true);
                        player.sendMessage(ChatColor.RED + "You Don't Have Permissions To Place Covered Chests");
                    }
            }
        }
    }

    boolean isNextToDoubleChest(Block block) {
        ArrayList<Block> list = getAdjacentChestBlocks(block);
        ArrayList<Block> list2;

        if(list.size() > 2)
            return false;

        if(list.size() > 0)
            for(Block b : list) {
                list2 = getAdjacentChestBlocks(b);

                if(list2.size() > 0)
                    return true;
            }

        return false;
    }

    ArrayList<Block> getAdjacentChestBlocks(Block block) {
        ArrayList<Block> list = new ArrayList<Block>();

        World w = block.getWorld();

        Integer x = block.getX();
        Integer y = block.getY();
        Integer z = block.getZ();

        Block[] bArr = { w.getBlockAt(x + 1, y, z - 1), w.getBlockAt(x - 1, y, z + 1),
                w.getBlockAt(x + 1, y, z), w.getBlockAt(x - 1, y, z),
                w.getBlockAt(x, y, z + 1), w.getBlockAt(x, y, z - 1) };

        for (Integer i = 0; i < 6; i++)
            if (bArr[i].getState() instanceof Chest)
                list.add(bArr[i]);

        return list;
    }

    void openSingleChest(Block block, Player player) {
        Inventory inv = ((Chest) block.getState()).getInventory();

        CraftInventory cInventory = (CraftInventory) inv;
        CraftPlayer cPlayer = (CraftPlayer) player;

        cPlayer.getHandle().openContainer(cInventory.getInventory());
    }

    Block[] getDoubleChestOrder(Block block1, Block block2){
        if(block1.getX()<block2.getX() || block1.getZ() < block2.getZ())
            return new Block[]{block1,block2};

        return new Block[]{block2,block1};


    }

    void openDoubleChest(Block block1, Block block2, Player player) {
        Block[] blocks = getDoubleChestOrder(block1,block2);

        Chest chest1 = (Chest)blocks[0].getState();
        Chest chest2 = (Chest)blocks[1].getState();

        CraftPlayer cPlayer = (CraftPlayer) player;

        CraftInventory cInventory1 = (CraftInventory) chest1.getInventory();
        CraftInventory cInventory2 = (CraftInventory) chest2.getInventory();

        IInventory IChest = new InventoryLargeChest("Large chest",
                cInventory1.getInventory(), cInventory2.getInventory());

        cPlayer.getHandle().openContainer(IChest);
    }

    boolean covered(Block block) {
        if (adjacentToChest(block)) {
            Block adjBlock = getAdjacentChestBlock(block);
            if (solidBlock(getBlockAbove(adjBlock))
                    || solidBlock(getBlockAbove(block)))
                return true;

        } else
            if (solidBlock(getBlockAbove(block)))
                return true;

        return false;
    }

    boolean tripleChest(Block block) {
        Integer count = 1;

        if (block.getRelative(1, 0, 0).getType() == Material.CHEST)
            count++;

        if (block.getRelative(-1, 0, 0).getType() == Material.CHEST)
            count++;

        if (block.getRelative(0, 0, 1).getType() == Material.CHEST)
            count++;

        if (block.getRelative(0, 0, -1).getType() == Material.CHEST)
            count++;

        return count > 2;
    }

    Block getBlockAbove(Block block) {
        return block.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ());
    }

    boolean adjacentToChest(Block block) {
        return getAdjacentChestBlock(block) != null;
    }

    Block getAdjacentChestBlock(Block block) {
        World w = block.getWorld();
        Integer x = block.getX();
        Integer y = block.getY();
        Integer z = block.getZ();
        Block[] bArr = { w.getBlockAt(x + 1, y, z), w.getBlockAt(x - 1, y, z),
                w.getBlockAt(x, y, z + 1), w.getBlockAt(x, y, z - 1) };

        for (Integer i = 0; i < 4; i++)
            if (bArr[i].getState() instanceof Chest)
                return bArr[i];

        return null;
    }

    boolean solidBlock(Block block) {
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
