package clashsoft.playerinventoryapi.client.gui;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

import clashsoft.playerinventoryapi.api.IButtonHandler;
import clashsoft.playerinventoryapi.api.invobject.InventoryObject;
import clashsoft.playerinventoryapi.common.PacketSurvivalInventorySlotClick;
import clashsoft.playerinventoryapi.inventory.ContainerCreativeList;
import clashsoft.playerinventoryapi.inventory.ContainerCustomInventoryCreative;
import clashsoft.playerinventoryapi.inventory.ContainerCustomInventorySurvival;
import clashsoft.playerinventoryapi.lib.GuiHelper.GuiPos;
import clashsoft.playerinventoryapi.lib.GuiHelper.GuiSize;
import cpw.mods.fml.common.network.PacketDispatcher;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.renderer.InventoryEffectRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.stats.AchievementList;
import net.minecraft.util.StatCollector;

public class GuiCustomInventorySurvival extends InventoryEffectRenderer
{
	private float									mouseX;
	private float									mouseY;
	
	private final EntityPlayer						player;
	
	private static GuiSize							windowSize				= new GuiSize(176, 166);
	private static GuiPos							playerDisplayPos		= new GuiPos(25, 7);
	private static GuiPos							craftingArrowPos		= new GuiPos(125, 37);
	private static float							craftingArrowRotation	= 0F;
	private static boolean							drawCraftingLabel		= true;
	
	private final GuiPos[]							slotPositions;
	
	private static Map<GuiButton, IButtonHandler>	buttons					= new HashMap<GuiButton, IButtonHandler>();
	
	private static List<InventoryObject>			objects					= new ArrayList<InventoryObject>();
	
	public GuiCustomInventorySurvival(EntityPlayer player, ContainerCustomInventorySurvival container)
	{
		super(container);
		
		this.allowUserInput = true;
		this.player = player;
		this.player.addStat(AchievementList.openInventory, 1);
		
		this.slotPositions = ContainerCustomInventorySurvival.slotPositions;
	}
	
	public static void setWindowSize(int width, int height)
	{
		windowSize = new GuiSize(width, height);
	}
	
	public static void setPlayerDisplayPos(int x, int y)
	{
		playerDisplayPos = new GuiPos(x, y);
	}
	
	public static void setCraftArrowPos(int x, int y)
	{
		craftingArrowPos = new GuiPos(x, y);
	}
	
	public static void setCraftArrowRot(float r)
	{
		craftingArrowRotation = r;
	}
	
	public static void addButton(IButtonHandler handler, GuiButton button)
	{
		buttons.put(button, handler);
	}
	
	public static void addObject(InventoryObject object)
	{
		objects.add(object);
	}
	
	/**
	 * Called from the main game loop to update the screen.
	 */
	@Override
	public void updateScreen()
	{
		if (this.mc.playerController.isInCreativeMode())
			this.mc.displayGuiScreen(new GuiCustomInventoryCreative(this.player, new ContainerCreativeList(player), new ContainerCustomInventoryCreative(player.inventory, false, player)));
	}
	
	/**
	 * Adds the buttons (and other controls) to the screen in question.
	 */
	@Override
	public void initGui()
	{
		this.buttonList.clear();
		for (GuiButton button : buttons.keySet())
		{
			this.buttonList.add(button);
		}
		
		if (this.mc.playerController.isInCreativeMode())
			this.mc.displayGuiScreen(new GuiCustomInventoryCreative(this.player, new ContainerCreativeList(player), new ContainerCustomInventoryCreative(player.inventory, false, player)));
		else
			super.initGui();
	}
	
	/**
	 * Draw the foreground layer for the GuiContainer (everything in front of the items)
	 */
	@Override
	protected void drawGuiContainerForegroundLayer(int mouseX, int mouseY)
	{
		if (drawCraftingLabel)
		{
			int x = this.slotPositions[1].getX();
			int y = this.slotPositions[1].getY();
			this.fontRenderer.drawString(StatCollector.translateToLocal("container.crafting"), x - 1, y - 10, 4210752);
		}
	}
	
	/**
	 * Draws the screen and all the components in it.
	 */
	@Override
	public void drawScreen(int mouseX, int mouseY, float fpt)
	{
		super.drawScreen(mouseX, mouseY, fpt);
		this.mouseX = mouseX;
		this.mouseY = mouseY;
	}
	
	/**
	 * Draw the background layer for the GuiContainer (everything behind the items)
	 */
	@Override
	protected void drawGuiContainerBackgroundLayer(float fpt, int mouseX, int mouseY)
	{
		GL11.glColor4f(1.0F, 1.0F, 1.0F, 1.0F);
		this.mc.renderEngine.bindTexture(GuiCustomInventoryCreative.custominventory);
		int k = (this.width - 176 + (!this.mc.thePlayer.getActivePotionEffects().isEmpty() ? 120 : 0)) / 2;
		int l = (this.height - 166) / 2;
		int m = (this.width - windowSize.getWidth() + (!this.mc.thePlayer.getActivePotionEffects().isEmpty() ? 120 : 0)) / 2;
		this.drawBackgroundFrame(m, (this.height - windowSize.getHeight()) / 2, windowSize.getWidth(), windowSize.getHeight());
		GL11.glTranslatef(k, l, 0);
		this.drawCraftArrow(craftingArrowPos.getX(), craftingArrowPos.getY(), craftingArrowRotation);
		this.drawPlayerBackground(playerDisplayPos.getX(), playerDisplayPos.getY());
		for (GuiPos pos : slotPositions)
		{
			if (pos != null)
				this.drawSlot(pos.getX(), pos.getY());
		}
		for (InventoryObject object : objects)
		{
			if (object != null)
				object.render(this.width, this.height);
		}
		GL11.glTranslatef(-k, -l, 0);
		
		drawPlayerOnGui(this.mc, k + GuiCustomInventorySurvival.playerDisplayPos.getX() + 26, l + GuiCustomInventorySurvival.playerDisplayPos.getY() + 65, 30, k + GuiCustomInventorySurvival.playerDisplayPos.getX() + 26 - this.mouseX, l + GuiCustomInventorySurvival.playerDisplayPos.getY() + 65 - 50 - this.mouseY);
	}
	
	public void drawBackgroundFrame(int posX, int posY, int sizeX, int sizeY)
	{
		this.mc.renderEngine.bindTexture(GuiCustomInventoryCreative.custominventory);
		
		GL11.glTranslatef(posX, posY, 0F);
		
		GL11.glScalef(sizeX / 8F, sizeY / 8F, 1F); // Scale both
		this.drawTexturedModalRect(0, 0, 4, 4, 8, 8);
		GL11.glScalef(8F / sizeX, 1F, 1F); // Unscale X
		
		this.drawTexturedModalRect(0, 0, 0, 4, 8, 8);
		this.drawTexturedModalRect(sizeX - 8, 0, 8, 4, 8, 8);
		
		GL11.glScalef(sizeX / 8F, 8F / sizeY, 1F); // Scale X, Unscale Y
		
		this.drawTexturedModalRect(0, 0, 4, 0, 8, 8);
		this.drawTexturedModalRect(0, sizeY - 8, 4, 8, 8, 8);
		
		GL11.glScalef(8F / sizeX, 1F, 1F); // Unscale X
		
		// Edges
		this.drawTexturedModalRect(0, 0, 0, 0, 8, 8);
		this.drawTexturedModalRect(sizeX - 8, 0, 8, 0, 8, 8);
		this.drawTexturedModalRect(0, sizeY - 8, 0, 8, 8, 8);
		this.drawTexturedModalRect(sizeX - 8, sizeY - 8, 8, 8, 8, 8);
		
		GL11.glTranslatef(-posX, -posY, 0F);
	}
	
	public void drawPlayerBackground(int posX, int posY)
	{
		this.mc.renderEngine.bindTexture(GuiCustomInventoryCreative.custominventory);
		this.drawTexturedModalRect(posX, posY, 0, 19, 54, 72);
	}
	
	public void drawCraftArrow(int posX, int posY, float rotation)
	{
		GL11.glPushMatrix();
		GL11.glTranslatef(posX + 8, posY + 7, 0F);
		GL11.glRotatef(craftingArrowRotation, 0, 0, 1);
		GL11.glTranslatef(-8F, -7F, 0F);
		this.drawTexturedModalRect(0, 0, 34, 0, 16, 13);
		GL11.glPopMatrix();
	}
	
	public void drawSlot(int posX, int posY)
	{
		this.mc.renderEngine.bindTexture(GuiCustomInventoryCreative.custominventory);
		this.drawTexturedModalRect(posX - 1, posY - 1, 16, 0, 18, 18);
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
		GL11.glRotatef(-((float) Math.atan(mouseY / 40.0F)) * 20.0F, 1.0F, 0.0F, 0.0F);
		
		mc.thePlayer.renderYawOffset = (float) Math.atan(mouseX / 40.0F) * 20.0F;
		mc.thePlayer.rotationYaw = (float) Math.atan(mouseX / 40.0F) * 40.0F;
		mc.thePlayer.rotationPitch = -((float) Math.atan(mouseY / 40.0F)) * 20.0F;
		
		if (Keyboard.isKeyDown(Keyboard.KEY_LSHIFT))
		{
			mc.thePlayer.rotationYaw = (mc.thePlayer.rotationYaw + 180) % 360;
			mc.thePlayer.renderYawOffset = (mc.thePlayer.renderYawOffset + 180) % 360;
			mc.thePlayer.rotationPitch = -mc.thePlayer.rotationPitch;
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
	
	/**
	 * Fired when a control is clicked. This is the equivalent of ActionListener.actionPerformed(ActionEvent e).
	 */
	@Override
	protected void actionPerformed(GuiButton button)
	{
		buttons.get(button).onButtonPressed(button);
	}
	
	@Override
	protected void handleMouseClick(Slot slot, int slotID, int var1, int var2)
	{
		if (slot != null)
			slotID = slot.slotNumber;
		this.inventorySlots.slotClick(slotID, var1, var2, this.player);
		PacketDispatcher.sendPacketToServer(new PacketSurvivalInventorySlotClick(slotID, var1, var2));
	}
}
