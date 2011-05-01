package com.mjmr89.Cabinet;

import net.minecraft.server.IInventory;
import net.minecraft.server.InventoryLargeChest;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.craftbukkit.inventory.CraftInventory;
import org.bukkit.entity.Player;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.inventory.Inventory;

public class CabinetBlockListener extends PlayerListener
{
	Cabinet plugin;

	CabinetBlockListener(Cabinet plugin)
	{
		this.plugin = plugin;
	}

	public void onPlayerInteract(PlayerInteractEvent e)
	{
		Player p = e.getPlayer();

		if (e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			e.isCancelled();
			e.setCancelled(false);
			Block b = e.getClickedBlock();

			if (((b.getState() instanceof Chest)) && (covered(b)))
			{
				if (adjacentToChest(b))
				{
					Block b2 = getAdjacentChestBlock(b);
					openDoubleChest(b, b2, p);
				}
				else
				{
					openSingleChest(b, p);
				}
			}
		}
	}

	void openSingleChest(Block b, Player p)
	{
		Inventory inv = ((Chest)b.getState()).getInventory();

		CraftInventory cInventory = (CraftInventory)inv;
		CraftPlayer cPlayer = (CraftPlayer)p;
		cPlayer.getHandle().a(cInventory.getInventory());
	}

	Block[] getDoubleChestOrder(Block b1, Block b2)
	{
		if ((b1.getX() < b2.getX()) || (b1.getZ() < b2.getZ()))
		{
			return new Block[] { b1, b2 };
		}
		return new Block[] { b2, b1 };
	}

	void openDoubleChest(Block b1, Block b2, Player p)
	{
		Block[] blocks = getDoubleChestOrder(b1, b2);
		Chest c1 = (Chest)blocks[0].getState();
		Chest c2 = (Chest)blocks[1].getState();
		CraftPlayer cPlayer = (CraftPlayer)p;

		CraftInventory cInventory1 = (CraftInventory)c1.getInventory();
		CraftInventory cInventory2 = (CraftInventory)c2.getInventory();
		IInventory IChest = new InventoryLargeChest("Large chest",
				cInventory1.getInventory(), cInventory2.getInventory());
		cPlayer.getHandle().a(IChest);
	}

	boolean covered(Block b)
	{
		if (adjacentToChest(b))
		{
			Block adjBlock = getAdjacentChestBlock(b);
			if ((solidBlock(getBlockAbove(adjBlock))) || (solidBlock(getBlockAbove(b))))
			{
				return true;
			}

		}
		else if (solidBlock(getBlockAbove(b)))
		{
			return true;
		}
		return false;
	}

	Block getBlockAbove(Block b)
	{
		return b.getWorld().getBlockAt(b.getX(), b.getY() + 1, b.getZ());
	}

	boolean adjacentToChest(Block b)
	{
		return getAdjacentChestBlock(b) != null;
	}

	Block getAdjacentChestBlock(Block b)
	{
		World w = b.getWorld();
		int x = b.getX();
		int y = b.getY();
		int z = b.getZ();
		Block[] bArr = { w.getBlockAt(x + 1, y, z), w.getBlockAt(x - 1, y, z),
				w.getBlockAt(x, y, z + 1), w.getBlockAt(x, y, z - 1) };
		for (int i = 0; i < 4; i++)
		{
			if ((bArr[i].getState() instanceof Chest))
			{
				return bArr[i];
			}
		}
		return null;
	}

	boolean isPartiallyCovered(Block b)
	{
		int x = b.getX();
		int y = b.getY();
		int z = b.getZ();
		World w = b.getWorld();

		Block[] bAboves = { w.getBlockAt(x + 1, y + 1, z),
			w.getBlockAt(x - 1, y + 1, z), w.getBlockAt(x, y + 1, z + 1),
			w.getBlockAt(x, y + 1, z - 1) };
		for (int i = 0; i < 4; i++)
		{
			if ((solidBlock(bAboves[i])) &&
				 ((w.getBlockAt(bAboves[i].getX(), bAboves[i].getY() - 1,
						 bAboves[i].getZ()).getState() instanceof Chest)))
			{
				return true;
			}
		}
		return false;
	}

  boolean solidBlock(Block b)
  {
	  switch (b.getType())
	  {
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
