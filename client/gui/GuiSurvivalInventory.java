package clashsoft.playerinventoryapi.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import clashsoft.cslib.math.Point2i;
import clashsoft.cslib.minecraft.client.gui.GuiBuilder;
import clashsoft.playerinventoryapi.api.IButtonHandler;
import clashsoft.playerinventoryapi.api.invobject.InventoryObject;
import clashsoft.playerinventoryapi.inventory.ContainerCreativeList;
import clashsoft.playerinventoryapi.inventory.ContainerInventory;
import clashsoft.playerinventoryapi.inventory.InventorySlots;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.StatCollector;

public class GuiSurvivalInventory extends GuiBasicInventory
{
	protected final EntityPlayer					player;
	
	// PLAYER INVENTORY API
	
	public static int								windowWidth				= 176;
	public static int								windowHeight			= 166;
	
	public static int								playerDisplayX			= 25;
	public static int								playerDisplayY			= 7;
	
	public static int								craftingArrowX			= 125;
	public static int								craftingArrowY			= 37;
	public static float								craftingArrowRotation	= 0F;
	
	public static int								craftingLabelX			= 87;
	public static int								craftingLabelY			= 16;
	
	protected static Map<GuiButton, IButtonHandler>	buttons					= new HashMap<GuiButton, IButtonHandler>();
	protected static List<InventoryObject>			objects					= new ArrayList<InventoryObject>();
	
	public GuiSurvivalInventory(EntityPlayer player, ContainerInventory container)
	{
		super(container);
		
		container.onContainerOpened(player);
		
		this.allowUserInput = true;
		this.player = player;
		this.player.addStat(AchievementList.openInventory, 1);
	}
	
	public static void addButton(IButtonHandler handler, GuiButton button)
	{
		buttons.put(button, handler);
	}
	
	public static void addObject(InventoryObject object)
	{
		objects.add(object);
	}
	
	@Override
	public void updateScreen()
	{
		super.updateScreen();
		
		if (this.mc.playerController.isInCreativeMode())
		{
			this.mc.displayGuiScreen(new GuiCreativeInventory(this.player, new ContainerCreativeList(this.player), (ContainerInventory) this.inventorySlots));
		}
	}
	
	@Override
	public void initGui()
	{
		this.buttonList.clear();
		for (GuiButton button : buttons.keySet())
		{
			this.buttonList.add(button);
		}
		
		if (this.mc.playerController.isInCreativeMode())
		{
			this.mc.displayGuiScreen(new GuiCreativeInventory(this.player, new ContainerCreativeList(this.player), (ContainerInventory) this.inventorySlots));
		}
		else
		{
			super.initGui();
		}
	}
	
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		super.drawGuiContainerForegroundLayer(mouseX, mouseY);
		
		int x = craftingLabelX;
		int y = craftingLabelY;
		if (x >= 0 && y >= 0)
		{
			this.fontRendererObj.drawString(StatCollector.translateToLocal("container.crafting"), x, y, 4210752);
		}
		
		if (this.func_146978_c(playerDisplayX, playerDisplayY, 54, 72, mouseX, mouseY))
		{
			this.drawPlayerHoveringText(this.player, mouseX, mouseY, this.fontRendererObj);
		}
	}
	
	@Override
	protected void drawGuiContainerBackgroundLayer(float patialTickTime, int mouseX, int mouseY)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		int k = this.guiLeft;
		int l = this.guiTop;
		
		GL11.glTranslatef(k, l, 0F);
		
		// Background Frame
		this.drawBackgroundFrame(0, 0, windowWidth, windowHeight);
		
		this.drawCraftArrow(craftingArrowX, craftingArrowY, craftingArrowRotation);
		
		// Player
		this.drawPlayerBackground(playerDisplayX, playerDisplayY);
		drawPlayerOnGui(this.mc, playerDisplayX + 26, playerDisplayY + 65, 30, k + playerDisplayX + 26 - mouseX, l + playerDisplayY + 15 - mouseY);
		
		// Slots
		for (Point2i pos : InventorySlots.survivalSlots)
		{
			if (pos != null)
			{
				this.drawSlot(pos.getX(), pos.getY());
			}
		}
		
		GL11.glTranslatef(-k, -l, 0);
		
		// Objects
		for (InventoryObject object : objects)
		{
			if (object != null)
			{
				object.render(this.width, this.height);
			}
		}
	}
	
	public void drawCraftArrow(int posX, int posY, float rotation)
	{
		this.mc.renderEngine.bindTexture(GuiBuilder.progress);
		GL11.glPushMatrix();
		GL11.glTranslatef(posX + 8, posY + 7, 0F);
		GL11.glRotatef(craftingArrowRotation, 0, 0, 1);
		GL11.glTranslatef(-8F, -7F, 0F);
		this.drawTexturedModalRect(0, 0, 0, 16, 16, 14);
		GL11.glPopMatrix();
	}
	
	public static void drawPlayerOnGui(Minecraft mc, int x, int y, int size, float mouseX, float mouseY)
	{
		GL11.glEnable(GL11.GL_COLOR_MATERIAL);
		GL11.glPushMatrix();
		GL11.glTranslatef(x, y, 50.0F);
		GL11.glScalef(-size, size, size);
		GL11.glRotatef(180.0F, 0.0F, 0.0F, 1.0F);
		float f2 = mc.thePlayer.renderYawOffset;
		float f3 = mc.thePlayer.rotationYaw;
		float f4 = mc.thePlayer.rotationPitch;
		GL11.glRotatef(135.0F, 0.0F, 1.0F, 0.0F);
		RenderHelper.enableStandardItemLighting();
		GL11.glRotatef(-135.0F, 0.0F, 1.0F, 0.0F);
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LMENU))
		{
			mc.thePlayer.rotationYaw = (System.currentTimeMillis() / 10) % 360;
			mc.thePlayer.renderYawOffset = mc.thePlayer.rotationYaw;
			mc.thePlayer.rotationPitch = 0;
		}
		else
		{
			GL11.glRotatef(-((float) Math.atan(mouseY / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
			
			mc.thePlayer.renderYawOffset = (float) Math.atan(mouseX / 40.0F) * 20.0F;
			mc.thePlayer.rotationYaw = (float) Math.atan(mouseX / 40.0F) * 40.0F;
			mc.thePlayer.rotationPitch = -((float) Math.atan(mouseY / 40.0F)) * 20.0F;
		}
		
		mc.thePlayer.rotationYawHead = mc.thePlayer.rotationYaw;
		GL11.glTranslatef(0.0F, mc.thePlayer.yOffset, 0.0F);
		RenderManager.instance.playerViewY = 180.0F;
		RenderManager.instance.renderEntityWithPosYaw(mc.thePlayer, 0.0D, 0.0D, 0.0D, 0.0F, 1.0F);
		mc.thePlayer.renderYawOffset = f2;
		mc.thePlayer.rotationYaw = f3;
		mc.thePlayer.rotationPitch = f4;
		GL11.glPopMatrix();
		RenderHelper.disableStandardItemLighting();
		GL11.glDisable(GL12.GL_RESCALE_NORMAL);
		OpenGlHelper.setActiveTexture(OpenGlHelper.lightmapTexUnit);
		GL11.glDisable(GL11.GL_TEXTURE_2D);
		OpenGlHelper.setActiveTexture(OpenGlHelper.defaultTexUnit);
	}
	
	@Override
	protected void handleMouseClick(Slot slot, int i, int j, int k)
	{
		if (slot != null)
		{
			i = slot.slotNumber;
		}
		if (k == 4 && !Keyboard.isKeyDown(this.mc.gameSettings.keyBindDrop.getKeyCode()))
		{
			k = isShiftKeyDown() ? 1 : 0;
		}
		
		this.player.openContainer = this.inventorySlots;
		super.handleMouseClick(slot, i, j, k);
	}
	
	@Override
	protected void actionPerformed(GuiButton button)
	{
		IButtonHandler handler = buttons.get(button);
		if (handler != null)
		{
			handler.onButtonPressed(button);
		}
	}
}