package clashsoft.playerinventoryapi.api.invobject;

import org.lwjgl.opengl.GL11;

public abstract class RotatableObject implements InventoryObject
{
	public float	rotation;
	
	@Override
	public final void render(int width, int height)
	{
		GL11.glPushMatrix();
		
		GL11.glTranslatef(this.getX(), this.getY(), 0F);
		GL11.glRotatef(this.rotation, 0F, 0F, 0F);
		GL11.glTranslatef(-this.getWidth() / 2, -this.getHeight() / 2, 0F);
		this.renderRotated(width, height);
		
		GL11.glPopMatrix();
	}
	
	public abstract void renderRotated(int width, int height);
	
	public abstract int getX();
	
	public abstract int getY();
	
	public abstract int getWidth();
	
	public abstract int getHeight();
}
