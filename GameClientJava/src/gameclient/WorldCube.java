package gameclient;

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.PlaneCollisionShape;
import com.jme3.math.Plane;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.renderer.ViewPort;
import com.jme3.scene.Geometry;
import com.jme3.scene.Spatial;
import com.jme3.scene.control.AbstractControl;
import com.jme3.scene.control.Control;

/**
 * @author Chris
 *         Describes a cube of the world. This will be a certain 3d cube of blocks
 */
public class WorldCube extends AbstractControl{
	public static final int size = 5; // size of the cube in blocks
	public static final int width = 3; // world unit size of the block
	private static final Vector3f[] vertices = new Vector3f[8];
	{
		vertices[0] = new Vector3f(0, 0, 0);
		vertices[1] = new Vector3f(width, 0, 0);
		vertices[2] = new Vector3f(0, width, 0);
		vertices[3] = new Vector3f(width, width, 0);
		vertices[4] = new Vector3f(0, width, width);
		vertices[5] = new Vector3f(width, width, width);
		vertices[6] = new Vector3f(0, 0, width);
		vertices[7] = new Vector3f(width, 0, width);
	}
	// int[] indexes = { 2, 3, 0, 3, 1, 0, 5, 3, 2, 4, 5, 2, 5, 1, 3, 4, 2, 0, 7, 1, 5, 6, 4, 0, 6, 7, 5, 4, 6, 5, 7, 6, 0, 7, 0, 1 };
	private static final int[] top = {4, 5, 2, 5, 3, 2};
	private static final int[] bot = {7, 6, 0, 7, 0, 1};
	private static final int[] frt = {4, 2, 6, 6, 2, 0};
	private static final int[] bck = {5, 7, 3, 3, 7, 1};
	private static final int[] lft = {3, 1, 0, 3, 0, 2};
	private static final int[] rht = {4, 6, 5, 5, 6, 7};

	WorldCube CubeTop = null; // TODO: change to function calls in the class that will hold the WorldCubes
	WorldCube CubeBot = null;
	WorldCube CubeFrt = null;
	WorldCube CubeBck = null;
	WorldCube CubeLft = null;
	WorldCube CubeRht = null;

	Vector2f[] BlacktexCoord = new Vector2f[4];
	{
		BlacktexCoord[0] = new Vector2f(0, 0);
		BlacktexCoord[1] = new Vector2f(.5f, 0);
		BlacktexCoord[2] = new Vector2f(0, 1);
		BlacktexCoord[3] = new Vector2f(.5f, 1);
	}

	Vector2f[] GreentexCoord = new Vector2f[4];
	{
		GreentexCoord[0] = new Vector2f(.5f, 0);
		GreentexCoord[1] = new Vector2f(1, 0);
		GreentexCoord[2] = new Vector2f(.5f, 1);
		GreentexCoord[3] = new Vector2f(1, 1);
	}

	int[] Ttop = {1, 3, 0, 3, 2, 0};
	int[] Tbot = {2, 0, 1, 2, 1, 3};
	int[] Tfrt = {3, 2, 1, 1, 2, 0};
	int[] Tbck = {2, 0, 3, 3, 0, 1};
	int[] Tlft = {2, 0, 1, 2, 1, 3};
	int[] Trht = {2, 0, 3, 3, 0, 1};

	CustomMesh myMesh = new CustomMesh(300);
	CompoundCollisionShape geomShape = new CompoundCollisionShape();

	private int[][][] blocks = new int[size][size][size];

	public WorldCube(int type) {
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				for (int z = 0; z < size; z++) {
					blocks[x][y][z] = type;
				}
			}
		}
	}
	public WorldCube(){} // empty serialization constructor
	
	@Override
	  public void setSpatial(Spatial spatial) { // init routine
	    super.setSpatial(spatial);
	    
	  }

	public int getBlockType(int x, int y, int z) {
		return blocks[x][y][z];
	}
	public static void getBlockInd(Vector3f hit) {
		float temp = hit.x-(int)hit.x;
		if(temp<0.0001||temp>-0.0001){
			System.out.println("Is boarder");
		}
	}

	public void setType(int x, int y, int z, int type) {
		blocks[x][y][z] = type;
	}
	public CustomMesh getMesh() {

		return (myMesh);
	}
	public CompoundCollisionShape getCosShape() {

		return (geomShape);
	}
	public void generateMesh() {// TODO: bulk transfer the boundary chunks blocks, optimize physics (panels, only for areas around player)
		myMesh.setDynamic();
		Vector3f box_size = new Vector3f(1.5f, 1.5f, 1.5f); // half size of box also used to offset box
		int temptype = 0;
		boolean addedFace = false;  // flag to see if it added a face for the block. if not dont add physics box
		for (int x = 0; x < size; x++) {
			for (int y = 0; y < size; y++) {
				for (int z = 0; z < size; z++) { // this z is up
					if (blocks[x][y][z] == 0) {
						addedFace=false;
						System.out.println("Block: " + x + " " + y + " " + z);
						temptype = 1; // default is to draw
						//geomShape.addChildShape(new BoxCollisionShape(box_size), box_size.add(x * width, z * width, y * width)); // adding physics shape - change to
						// adding panels
						if (z + 1 == size) { // see if i need to check next cube
							if (CubeTop == null) { // if none there
								temptype = 1;// draw
							} else { // else see if cube there
								temptype = CubeTop.getBlockType(x, y, 0);

							}
						} else if (blocks[x][y][z + 1] == 0) {// or if not on boundary check next cube;
							temptype = 0;// dont draw
						} else {
							temptype = 1;
						}
						if (temptype != 0) {
							for (int i = 0; i < 6; i++) {
								myMesh.setTexCoord(BlacktexCoord[Ttop[i]]);
								myMesh.addVertex(vertices[top[i]].add(x * width, z * width, y * width));
								addedFace=true;
							}
							/*
							 * Plane tempP = new Plane();
							 * tempP.setPlanePoints(vertices[top[0]].add(x * width, z * width, y * width), vertices[top[1]].add(x * width, z * width, y * width),
							 * vertices[top[2]].add(x * width, z * width, y * width));
							 * geomShape.addChildShape(new PlaneCollisionShape(tempP), vertices[top[0]].add(x * width, z * width, y * width));
							 */
						}
						if (z == 0) { // see if i need to check next cube
							if (CubeBot == null) { // if none there
								temptype = 1;
							} else { // else see if cube there
								temptype = CubeBot.getBlockType(x, y, size - 1);
							}
						} else if (blocks[x][y][z - 1] == 0) {// or if not on boundary check next cube;
							temptype = 0;
						} else {
							temptype = 1;
						}
						if (temptype != 0) {
							for (int i = 0; i < 6; i++) {
								myMesh.setTexCoord(BlacktexCoord[Tbot[i]]);
								myMesh.addVertex(vertices[bot[i]].add(x * width, z * width, y * width));
								addedFace=true;
							}
						}

						if (x == 0) { // see if i need to check next cube
							if (CubeFrt == null) { // if none there
								temptype = 1;
							} else { // else see if cube there
								temptype = CubeFrt.getBlockType(size - 1, y, z);
							}
						} else if (blocks[x - 1][y][z] == 0) {// or if not on boundary check next cube;
							temptype = 0;
						} else {
							temptype = 1;
						}
						if (temptype != 0) {
							for (int i = 0; i < 6; i++) {
								myMesh.setTexCoord(BlacktexCoord[Tfrt[i]]);
								myMesh.addVertex(vertices[frt[i]].add(x * width, z * width, y * width));
								addedFace=true;
							}
						}

						if (x + 1 == size) { // see if i need to check next cube
							if (CubeBck == null) { // if none there
								temptype = 1;
							} else { // else see if cube there
								temptype = CubeBck.getBlockType(0, y, z);
							}
						} else if (blocks[x + 1][y][z] == 0) {// or if not on boundary check next cube;
							temptype = 0;
						} else {
							temptype = 1;
						}
						if (temptype != 0) {
							for (int i = 0; i < 6; i++) {
								myMesh.setTexCoord(GreentexCoord[Tbck[i]]);
								myMesh.addVertex(vertices[bck[i]].add(x * width, z * width, y * width));
								addedFace=true;
							}
						}

						if (y + 1 == size) { // see if i need to check next cube
							if (CubeRht == null) { // if none there
								temptype = 1;
							} else { // else see if cube there
								temptype = CubeRht.getBlockType(x, 0, z);
							}
						} else if (blocks[x][y + 1][z] == 0) {// or if not on boundary check next cube;
							temptype = 0;
						} else {
							temptype = 1;
						}
						if (temptype != 0) {
							for (int i = 0; i < 6; i++) {
								myMesh.setTexCoord(GreentexCoord[Trht[i]]);
								myMesh.addVertex(vertices[rht[i]].add(x * width, z * width, y * width));
								addedFace=true;
							}
						}

						if (y == 0) { // see if i need to check next cube
							if (CubeLft == null) { // if none there
								temptype = 1;
							} else { // else see if cube there
								temptype = CubeLft.getBlockType(x, size - 1, z);
							}
						} else if (blocks[x][y - 1][z] == 0) {// or if not on boundary check next cube;
							temptype = 0;
						} else {
							temptype = 1;
						}
						if (temptype != 0) {
							for (int i = 0; i < 6; i++) {
								myMesh.setTexCoord(GreentexCoord[Tlft[i]]);
								myMesh.addVertex(vertices[lft[i]].add(x * width, z * width, y * width));
								addedFace=true;
							}
						}
						if(addedFace){
							geomShape.addChildShape(new BoxCollisionShape(box_size), box_size.add(x * width, z * width, y * width));
						}
					}
				}
			}
		}
		myMesh.finish();
		myMesh.setStatic();

	}
	public void setDebugCube() { // create an alternating pattern
		for (int i = 0; i < (WorldCube.size); i++) {
			for (int j = 0; j < (WorldCube.size); j++) {
				for (int k = 0; k < (WorldCube.size); k++) {
					this.setType(i, j, k, (i + j + k) % 2);
				}
			}
		}
		// generateMesh();
	}
	public void setDebugFloor() { // create a floor - only cubes on bottom, must be init with 1 in constructor

		for (int j = 0; j < (WorldCube.size); j++) {
			for (int k = 0; k < (WorldCube.size); k++) {
				this.setType(j, k, 0, 0);
			}
		}
	}

	public WorldCube getCubeTop() {
		return CubeTop;
	}

	public WorldCube getCubeBot() {
		return CubeBot;
	}

	public WorldCube getCubeFrt() {
		return CubeFrt;
	}

	public WorldCube getCubeBck() {
		return CubeBck;
	}

	public WorldCube getCubeLft() {
		return CubeLft;
	}

	public WorldCube getCubeRht() {
		return CubeRht;
	}

	public void setCubeTop(WorldCube cubeTop) {
		CubeTop = cubeTop;
	}

	public void setCubeBot(WorldCube cubeBot) {
		CubeBot = cubeBot;
	}

	public void setCubeFrt(WorldCube cubeFrt) {
		CubeFrt = cubeFrt;
	}

	public void setCubeBck(WorldCube cubeBck) {
		CubeBck = cubeBck;
	}

	public void setCubeLft(WorldCube cubeLft) {
		CubeLft = cubeLft;
	}

	public void setCubeRht(WorldCube cubeRht) {
		CubeRht = cubeRht;
	}

	@Override
	public Control cloneForSpatial(Spatial arg0) {
		final Control myWC = new WorldCube();
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected void controlRender(RenderManager arg0, ViewPort arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void controlUpdate(float arg0) {
		// TODO Auto-generated method stub
		
	}
}
