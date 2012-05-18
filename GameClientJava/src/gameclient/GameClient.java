/**
 * Currently basic foundation for the client. Being used to test connections to the server
 */
package gameclient;

import java.io.IOException;

import com.jme3.bullet.collision.shapes.BoxCollisionShape;
import com.jme3.bullet.collision.shapes.CollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.collision.shapes.MeshCollisionShape;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.input.KeyInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Mesh.Mode;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Box;
import com.jme3.texture.Texture;
import com.jme3.util.BufferUtils;

/**
 * @author Chris
 * 
 */
public class GameClient extends SimpleApplication implements ActionListener {
	/**
	 * 
	 */
	ConnectionHandler myCH = null;
	Geometry geom, g2 = null;
	Node world = null;
	private BulletAppState bulletAppState;
	private RigidBodyControl floor_phy;
	private Box floor;
	Material floor_mat;
	private CharacterControl player;
	private Vector3f walkDirection = new Vector3f();
	private boolean left = false, right = false, up = false, down = false;
	int[][] test;
	GameWorld myGW = null;

	public GameClient() {
		myCH = new ConnectionHandler();
		myGW = new GameWorld();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) throws IOException {

		GameClient app = new GameClient();

		app.start();

	}

	@Override
	public void simpleInitApp() {
		this.assetManager.registerLocator("assets/", FileLocator.class);
		test = new int[9][9];
		for (int x = 0; x < 9; x++) {
			for (int y = 0; y < 9; y++) {
				test[x][y] = (x + y) % 2;
			}

		}
		/** Set up Physics */
		bulletAppState = new BulletAppState();
		stateManager.attach(bulletAppState);
		bulletAppState.getPhysicsSpace().enableDebug(assetManager);
		viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
		flyCam.setMoveSpeed(100);
		setUpKeys();
		setUpLight();
		initFloor();

		CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f,
				6f, 1);
		player = new CharacterControl(capsuleShape, 0.05f);
		player.setJumpSpeed(20);
		player.setFallSpeed(30);
		player.setGravity(30);
		player.setPhysicsLocation(new Vector3f(0, 0, 0));

		// We attach the scene and the player to the rootNode and the physics
		// space,
		// to make them appear in the game world.

		bulletAppState.getPhysicsSpace().add(player);

		world = new Node("world");

		Vector3f[] vertices = new Vector3f[8];
		vertices[0] = new Vector3f(0, 0, 0);
		vertices[1] = new Vector3f(3, 0, 0);
		vertices[2] = new Vector3f(0, 3, 0);
		vertices[3] = new Vector3f(3, 3, 0);
		vertices[4] = new Vector3f(0, 3, 3);
		vertices[5] = new Vector3f(3, 3, 3);
		vertices[6] = new Vector3f(0, 0, 3);
		vertices[7] = new Vector3f(3, 0, 3);
		//int[] indexes = { 2, 3, 0, 3, 1, 0, 5, 3, 2, 4, 5, 2, 5, 1, 3, 4, 2, 0, 7, 1, 5, 6, 4, 0, 6, 7, 5, 4, 6, 5, 7, 6, 0, 7, 0, 1 };
		int[] top = {4,5,2,5,3,2};
		int[] bot = {7,6,0,7,0,1};
		int[] frt = {4,2,6,6,2,0};
		int[] bck = {5,7,3,3,7,1};
		int[] lft = {3,1,0,3,0,2};
		int[] rht = {4,6,5,5,6,7};
		
		Vector2f[] BlacktexCoord = new Vector2f[4];
		BlacktexCoord[0] = new Vector2f(0,0);
		BlacktexCoord[1] = new Vector2f(.5f,0);
		BlacktexCoord[2] = new Vector2f(0,1);
		BlacktexCoord[3] = new Vector2f(.5f,1);
		
		Vector2f[] GreentexCoord = new Vector2f[4];
		GreentexCoord[0] = new Vector2f(.5f,0);
		GreentexCoord[1] = new Vector2f(1,0);
		GreentexCoord[2] = new Vector2f(.5f,1);
		GreentexCoord[3] = new Vector2f(1,1);
		
		int[] Ttop = {1,3,0,3,2,0};
		int[] Tbot = {2,0,1,2,1,3};
		int[] Tfrt = {3,2,1,1,2,0};
		int[] Tbck = {2,0,3,3,0,1};
		int[] Tlft = {2,0,1,2,1,3};
		int[] Trht = {2,0,3,3,0,1};
		
		CompoundCollisionShape geomShape = new CompoundCollisionShape();
		Vector3f box_size = new Vector3f(1.5f,1.5f,1.5f); //half size of box also used to offeset box
		
		CustomMesh myMesh = new CustomMesh(300);
		for (int x = 0; x < 3; x++) {
			for (int y = 0; y < 3; y++) {
				if (myGW.getCubeType(x, y, 0) == 0) {
					geomShape.addChildShape(new BoxCollisionShape(box_size), box_size.add(x*3,0,y*3));
					for (int i = 0; i < 6; i++) {
						myMesh.setTexCoord(BlacktexCoord[Ttop[i]]);
						myMesh.addVertex(vertices[top[i]].add(x * 3, 0, y * 3));
					}
					for (int i = 0; i < 6; i++) {
						myMesh.setTexCoord(BlacktexCoord[Tbot[i]]);
						myMesh.addVertex(vertices[bot[i]].add(x * 3, 0, y * 3));
					}
					for (int i = 0; i < 6; i++) {
						myMesh.setTexCoord(BlacktexCoord[Tfrt[i]]);
						myMesh.addVertex(vertices[frt[i]].add(x * 3, 0, y * 3));
					}
					for (int i = 0; i < 6; i++) {
						myMesh.setTexCoord(GreentexCoord[Tbck[i]]);
						myMesh.addVertex(vertices[bck[i]].add(x * 3, 0, y * 3));
					}
					for (int i = 0; i < 6; i++) {
						myMesh.setTexCoord(GreentexCoord[Tlft[i]]);
						myMesh.addVertex(vertices[lft[i]].add(x * 3, 0, y * 3));
					}
					for (int i = 0; i < 6; i++) {
						myMesh.setTexCoord(GreentexCoord[Trht[i]]);
						myMesh.addVertex(vertices[rht[i]].add(x * 3, 0, y * 3));
					}
				}
			}
		}
		myMesh.finish();
		
		Geometry geom = new Geometry("world",myMesh);
		 Material mat = new Material(assetManager,"Common/MatDefs/Misc/Unshaded.j3md");
		//mat.setColor("Color", ColorRGBA.Blue);
		 Texture tex_ml = assetManager.loadTexture("Textures/test.png");
		 mat.setTexture("ColorMap", tex_ml);
		 //mat.getAdditionalRenderState().setWireframe(true);
		 geom.setMaterial(mat);
		 world.attachChild(geom);
		// CollisionShape geomShape = CollisionShapeFactory.createMeshShape((Node) world );
		RigidBodyControl geom_phy = new RigidBodyControl(geomShape,0.0f);
		geom.addControl(geom_phy);
		bulletAppState.getPhysicsSpace().add(geom_phy);
		// TextureAtlas myTA = null;
		/*
		 * Box b = new Box(Vector3f.ZERO, 1, 1, 1); geom = new Geometry("Box",
		 * b); Geometry g3 = new Geometry("Quad", makeCube(new
		 * Vector3f(0,5,0))); Material mat = new Material(assetManager,
		 * "Common/MatDefs/Misc/Unshaded.j3md"); mat.setColor("Color",
		 * ColorRGBA.Blue); geom.setMaterial(mat);
		 * //mat.getAdditionalRenderState().setWireframe(true);
		 * g3.setMaterial(mat); g2 = geom.clone(); g2.setLocalTranslation(0, 0,
		 * 0); g2.getMaterial().setColor("Color", ColorRGBA.Green);
		 * //world.attachChild(geom); //world.attachChild(g2);
		 * world.attachChild(g3); world.setLocalTranslation(0, 0, -5);
		 */
		 world.setLocalTranslation(3, 2, 3);
		rootNode.attachChild(world);

	}

	private Mesh makeCube(Vector3f loc) {

		Mesh m = new Mesh();
		m.setMode(Mode.Triangles);
		// Vertex positions in space
		Vector3f[] vertices = new Vector3f[8 * 9];
		for (int x = 0; x < 9; x++) {

			vertices[(x * 8) + 0] = new Vector3f(0, 0, 0).add(loc);
			vertices[(x * 8) + 1] = new Vector3f(3, 0, 0).add(loc);
			vertices[(x * 8) + 2] = new Vector3f(0, 3, 0).add(loc);
			vertices[(x * 8) + 3] = new Vector3f(3, 3, 0).add(loc);
			vertices[(x * 8) + 4] = new Vector3f(0, 3, 3).add(loc);
			vertices[(x * 8) + 5] = new Vector3f(3, 3, 3).add(loc);
			vertices[(x * 8) + 6] = new Vector3f(0, 0, 3).add(loc);
			vertices[(x * 8) + 7] = new Vector3f(3, 0, 3).add(loc);
		}
		// Texture coordinates
		Vector2f[] texCoord = new Vector2f[4];
		texCoord[0] = new Vector2f(0, 0);
		texCoord[1] = new Vector2f(1, 0);
		texCoord[2] = new Vector2f(0, 1);
		texCoord[3] = new Vector2f(1, 1);

		// Indexes. We define the order in which mesh should be constructed
		int[] indexes = { 2, 3, 0, 3, 1, 0, 5, 3, 2, 4, 5, 2, 5, 1, 3, 4, 2, 0,
				7, 1, 5, 6, 4, 0, 6, 7, 5, 4, 6, 5, 7, 6, 0, 7, 0, 1 };
		// int [] indexes = {2,3,0,3,1,0};

		// Setting buffers
		m.setBuffer(Type.Position, 3, BufferUtils.createFloatBuffer(vertices));
		m.setBuffer(Type.TexCoord, 2, BufferUtils.createFloatBuffer(texCoord));
		m.setBuffer(Type.Index, 1, BufferUtils.createIntBuffer(indexes));

		m.updateBound();
		m.setStatic();
		return (m);
	}

	private Geometry makeCube2(Vector3f loc) {
		Box b = new Box(loc, 1, 1, 1);
		Geometry Rgeom = new Geometry("Box", b);
		Material mat = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		mat.setColor("Color", ColorRGBA.Blue);
		Rgeom.setMaterial(mat);
		return (Rgeom);
	}

	public void initFloor() {
		floor_mat = new Material(assetManager,
				"Common/MatDefs/Misc/Unshaded.j3md");
		floor_mat.setColor("Color", ColorRGBA.DarkGray);
		floor = new Box(Vector3f.ZERO, 100f, 0.1f, 50f);
		//floor.scaleTextureCoordinates(new Vector2f(3, 6));
		Geometry floor_geo = new Geometry("Floor", floor);
		floor_geo.setMaterial(floor_mat);
		floor_geo.setLocalTranslation(0, -0.1f, 0);
		this.rootNode.attachChild(floor_geo);
		/* Make the floor physical with mass 0.0f! */
		floor_phy = new RigidBodyControl(0.0f);
		floor_geo.addControl(floor_phy);
		bulletAppState.getPhysicsSpace().add(floor_phy);
	}

	private void setUpLight() {
		// We add light so we see the scene
		AmbientLight al = new AmbientLight();
		al.setColor(ColorRGBA.White.mult(1.3f));
		rootNode.addLight(al);

		DirectionalLight dl = new DirectionalLight();
		dl.setColor(ColorRGBA.White);
		dl.setDirection(new Vector3f(2.8f, -2.8f, -2.8f).normalizeLocal());
		rootNode.addLight(dl);
	}

	/**
	 * We over-write some navigational key mappings here, so we can add
	 * physics-controlled walking and jumping:
	 */
	private void setUpKeys() {
		inputManager.addMapping("Left", new KeyTrigger(KeyInput.KEY_A));
		inputManager.addMapping("Right", new KeyTrigger(KeyInput.KEY_D));
		inputManager.addMapping("Up", new KeyTrigger(KeyInput.KEY_W));
		inputManager.addMapping("Down", new KeyTrigger(KeyInput.KEY_S));
		inputManager.addMapping("Jump", new KeyTrigger(KeyInput.KEY_SPACE));
		inputManager.addListener(this, "Left");
		inputManager.addListener(this, "Right");
		inputManager.addListener(this, "Up");
		inputManager.addListener(this, "Down");
		inputManager.addListener(this, "Jump");
	}

	/**
	 * These are our custom actions triggered by key presses. We do not walk
	 * yet, we just keep track of the direction the user pressed.
	 */
	public void onAction(String binding, boolean value, float tpf) {
		if (binding.equals("Left")) {
			left = value;
		} else if (binding.equals("Right")) {
			right = value;
		} else if (binding.equals("Up")) {
			up = value;
		} else if (binding.equals("Down")) {
			down = value;
		} else if (binding.equals("Jump")) {
			player.jump();
		}
	}

	@Override
	public void simpleUpdate(float tpf) {
		// TODO: add update code
		// world.rotate(0, 2*tpf, 0);
		Vector3f camDir = cam.getDirection().clone().multLocal(0.6f);
		Vector3f camLeft = cam.getLeft().clone().multLocal(0.4f);
		walkDirection.set(0, 0, 0);
		if (left) {
			walkDirection.addLocal(camLeft);
		}
		if (right) {
			walkDirection.addLocal(camLeft.negate());
		}
		if (up) {
			walkDirection.addLocal(camDir);
		}
		if (down) {
			walkDirection.addLocal(camDir.negate());
		}
		player.setWalkDirection(walkDirection);
		cam.setLocation(player.getPhysicsLocation());
	}

	@Override
	public void simpleRender(RenderManager rm) {
		// TODO: add render code
	}

}
