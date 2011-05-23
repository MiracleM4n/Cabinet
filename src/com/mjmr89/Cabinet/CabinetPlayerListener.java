package com.mjmr89.Cabinet;

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
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.Inventory;

public class CabinetPlayerListener extends PlayerListener {
	Cabinet plugin;

	CabinetPlayerListener(Cabinet plugin) {
		this.plugin = plugin;
	}

	public void onPlayerInteract(PlayerInteractEvent event) {
		Player player = event.getPlayer();
	    Action action = event.getAction();

		if (action == Action.RIGHT_CLICK_BLOCK) {
			Block block = event.getClickedBlock();
		    int type = block.getTypeId();

			if (type == Material.CHEST.getId()) {
				if(event.useInteractedBlock() == Event.Result.DENY) {
					event.setCancelled(true);
					return;
				} else {
					if (((block.getState() instanceof Chest)) && (tripleChest(block))) {
						if (adjacentToChest(block)) {
							Block block2 = getAdjacentChestBlock(block);
							openDoubleChest(block, block2, player);
						} else {
							openSingleChest(block, player);
						}
						event.setCancelled(true);
					}
					if((Cabinet.Permissions == null && player.isOp()) || (Cabinet.Permissions != null && Cabinet.Permissions.has(player, "cabinet.covered"))) {
						if (((block.getState() instanceof Chest)) && (covered(block))) {
							if (adjacentToChest(block)) {
								Block block2 = getAdjacentChestBlock(block);
								openDoubleChest(block, block2, player);
							} else {
								openSingleChest(block, player);
							}
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
				airBlock = block.getFace(blockface);


				int firstSlot = player.getInventory().first(Material.CHEST);
				int num = player.getInventory().getItem(firstSlot).getAmount();
				int inf = player.getInventory().getItem(firstSlot).getMaxStackSize();

				if(isNextToDoubleChest(airBlock)) {
					if((Cabinet.Permissions == null && player.isOp()) || (Cabinet.Permissions != null && Cabinet.Permissions.has(player, "cabinet.adjchest"))) {
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
						if(num < 1) {
							return;
						}
					} else {
						player.sendMessage(ChatColor.RED + "You Don't Have Permissions To Place Over 2 Adjacent Chests");
					}
				}
			}
		}
	}
	
	boolean isNextToDoubleChest(Block block) {
		ArrayList<Block> list = getAdjacentChestBlocks(block);
		ArrayList<Block> list2;
		
		//plugin.getServer().broadcastMessage("list size: " + list.size());
		
		if(list.size() > 2) {
			return false;
		}
		
		if(list.size() > 0) {
			for(Block b : list) {
				list2 = getAdjacentChestBlocks(b);
				//plugin.getServer().broadcastMessage("list2 size: " + list2.size());
				
				if(list2.size()>0) {
					return true;
				}
			}
		}
		return false;
	}
	
	ArrayList<Block> getAdjacentChestBlocks(Block block) {
		ArrayList<Block> list = new ArrayList<Block>();
		World w = block.getWorld();
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		Block[] bArr = { w.getBlockAt(x + 1, y, z - 1), w.getBlockAt(x - 1, y, z + 1),
				w.getBlockAt(x + 1, y, z), w.getBlockAt(x - 1, y, z),
				w.getBlockAt(x, y, z + 1), w.getBlockAt(x, y, z - 1) };
		for (int i = 0; i < 6; i++) {
			if (bArr[i].getState() instanceof Chest) {
				list.add(bArr[i]);
			}
		}
		return list;
	}

	void openSingleChest(Block block, Player player) {
		Inventory inv = ((Chest) block.getState()).getInventory();

		CraftInventory cInventory = (CraftInventory) inv;
		CraftPlayer cPlayer = (CraftPlayer) player;
		cPlayer.getHandle().a((IInventory) cInventory.getInventory());
	}
	
	Block[] getDoubleChestOrder(Block block1, Block block2){
		if(block1.getX()<block2.getX() || block1.getZ() < block2.getZ()) {
			return new Block[]{block1,block2};
		}
		return new Block[]{block2,block1};
		
		
	}

	void openDoubleChest(Block block1, Block block2, Player player) {
		Block[] blocks = getDoubleChestOrder(block1,block2);
		Chest chest1 = (Chest)blocks[0].getState();
		Chest chest2 = (Chest)blocks[1].getState();
		CraftPlayer cPlayer = (CraftPlayer) player;

		CraftInventory cInventory1 = (CraftInventory) chest1.getInventory();
		CraftInventory cInventory2 = (CraftInventory) chest2.getInventory();
		IInventory IChest = (IInventory) new InventoryLargeChest("Large chest",
				cInventory1.getInventory(), cInventory2.getInventory());
		cPlayer.getHandle().a(IChest);
	}

	boolean covered(Block block) {
		if (adjacentToChest(block)) {
			Block adjBlock = getAdjacentChestBlock(block);
			if (solidBlock(getBlockAbove(adjBlock)) || solidBlock(getBlockAbove(block))) {
				return true;
			}
		} else {
			if (solidBlock(getBlockAbove(block))) {
				return true;
			}
		}
		return false;
	}

	boolean tripleChest(Block block) {
		int count = 1;
		if (block.getRelative(1, 0, 0).getType() == Material.CHEST) {
			count++;
		}
		if (block.getRelative(-1, 0, 0).getType() == Material.CHEST) {
			count++;
		}
		if (block.getRelative(0, 0, 1).getType() == Material.CHEST) {
			count++;
		}
		if (block.getRelative(0, 0, -1).getType() == Material.CHEST) {
			count++;
		}
		return count > 2;
	}

	Block getBlockAbove(Block block) {
		return block.getWorld().getBlockAt(block.getX(), block.getY() + 1, block.getZ());
	}

	boolean adjacentToChest(Block block) {
		if (getAdjacentChestBlock(block) == null) {
			return false;
		}
		return true;
	}

	Block getAdjacentChestBlock(Block block) {
		World w = block.getWorld();
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		Block[] bArr = { w.getBlockAt(x + 1, y, z), w.getBlockAt(x - 1, y, z),
				w.getBlockAt(x, y, z + 1), w.getBlockAt(x, y, z - 1) };
		for (int i = 0; i < 4; i++) {
			if (bArr[i].getState() instanceof Chest) {
				return bArr[i];
			}
		}
		return null;
	}

	boolean isPartiallyCovered(Block block) {
		int x = block.getX();
		int y = block.getY();
		int z = block.getZ();
		World w = block.getWorld();

		Block[] bAboves = { w.getBlockAt(x + 1, y + 1, z),
				w.getBlockAt(x - 1, y + 1, z), w.getBlockAt(x, y + 1, z + 1),
				w.getBlockAt(x, y + 1, z - 1) };
		for (int i = 0; i < 4; i++) {
			if (solidBlock(bAboves[i])
					&& w.getBlockAt(bAboves[i].getX(), bAboves[i].getY() - 1,
							bAboves[i].getZ()).getState() instanceof Chest) {
				return true;
			}
		}
		return false;
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
