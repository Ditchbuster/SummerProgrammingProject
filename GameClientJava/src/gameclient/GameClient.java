/**
 * Currently basic foundation for the client. Being used to test connections to the server
 */
package gameclient;

import java.io.IOException;

import com.bulletphysics.collision.shapes.CollisionShape;
import com.jme3.app.SimpleApplication;
import com.jme3.asset.plugins.FileLocator;
import com.jme3.bullet.BulletAppState;
import com.jme3.bullet.collision.shapes.CapsuleCollisionShape;
import com.jme3.bullet.collision.shapes.CompoundCollisionShape;
import com.jme3.bullet.control.CharacterControl;
import com.jme3.bullet.control.RigidBodyControl;
import com.jme3.bullet.util.CollisionShapeFactory;
import com.jme3.collision.CollisionResult;
import com.jme3.collision.CollisionResults;
import com.jme3.font.BitmapText;
import com.jme3.input.KeyInput;
import com.jme3.input.MouseInput;
import com.jme3.input.controls.ActionListener;
import com.jme3.input.controls.KeyTrigger;
import com.jme3.input.controls.MouseButtonTrigger;
import com.jme3.light.AmbientLight;
import com.jme3.light.DirectionalLight;
import com.jme3.material.Material;
import com.jme3.math.ColorRGBA;
import com.jme3.math.Ray;
import com.jme3.math.Vector2f;
import com.jme3.math.Vector3f;
import com.jme3.renderer.RenderManager;
import com.jme3.scene.Geometry;
import com.jme3.scene.Mesh;
import com.jme3.scene.Mesh.Mode;
import com.jme3.scene.Node;
import com.jme3.scene.VertexBuffer.Type;
import com.jme3.scene.shape.Box;
import com.jme3.scene.shape.Sphere;
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
	Geometry mark;

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
		//bulletAppState.getPhysicsSpace().enableDebug(assetManager);
		viewPort.setBackgroundColor(new ColorRGBA(0.7f, 0.8f, 1f, 1f));
		flyCam.setMoveSpeed(100);
		setUpKeys();
		initMouse();
		setUpLight();
		initFloor();
		initCrossHairs();
		initMark();

		CapsuleCollisionShape capsuleShape = new CapsuleCollisionShape(1.5f, 6f, 1);
		player = new CharacterControl(capsuleShape, 0.05f);
		player.setJumpSpeed(20);
		player.setFallSpeed(30);
		player.setGravity(30);
		player.setPhysicsLocation(new Vector3f(-10, 0, -10));

		// We attach the scene and the player to the rootNode and the physics
		// space,
		// to make them appear in the game world.

		bulletAppState.getPhysicsSpace().add(player);

		Node floor = new Node("floor");
		
		int count = 10;
		WorldCube[][] fl = new WorldCube[count][count];
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < count; j++) {
				fl[i][j] = new WorldCube(1);
				fl[i][j].setDebugFloor();
				if (i != 0) {
					fl[i][j].setCubeFrt(fl[i - 1][j]);
					fl[i - 1][j].setCubeBck(fl[i][j]);
				}
				if (j != 0) {
					fl[i][j].setCubeLft(fl[i][j - 1]);
					fl[i][j - 1].setCubeRht(fl[i][j]);
				}
			}
		}
		Material mat2 = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		// mat.setColor("Color", ColorRGBA.Blue);
		Texture tex_m2 = assetManager.loadTexture("Textures/test.png");
		mat2.setTexture("ColorMap", tex_m2);
		for (int i = 0; i < count; i++) {
			for (int j = 0; j < count; j++) {
			fl[i][j].generateMesh();	
			Geometry geom = new Geometry("fl"+i+j, fl[i][j].getMesh());
			geom.setMaterial(mat2);
			geom.setLocalTranslation(i*(WorldCube.size)*WorldCube.width, 0, j*(WorldCube.size)*WorldCube.width);
			floor.attachChild(geom);
			RigidBodyControl geom_phy = new RigidBodyControl(fl[i][j].getCosShape(), 0.0f);
			geom.addControl(geom_phy);
			bulletAppState.getPhysicsSpace().add(geom_phy);
			}
			}
		rootNode.attachChild(floor);
		
		
		world = new Node("world");
		world.attachChild(floor);
		WorldCube test = new WorldCube(0);
		WorldCube test2 = new WorldCube(0);
		WorldCube test3 = new WorldCube(0);
		test3.setDebugCube();
		test.setDebugCube();
		test.setCubeTop(test2);
		test.setCubeRht(test3);
		test2.setCubeBot(test);
		test3.setCubeLft(test);
		test.generateMesh();
		test2.generateMesh();
		test3.generateMesh();
		Geometry geom = new Geometry("C0", test.getMesh());
		geom.addControl(test);
		Geometry geom2 = new Geometry("C1", test2.getMesh());
		Geometry geom3 = new Geometry("C2", test3.getMesh());
		Material mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		// mat.setColor("Color", ColorRGBA.Blue);
		Texture tex_ml = assetManager.loadTexture("Textures/test.png");
		mat.setTexture("ColorMap", tex_ml);
		// mat.getAdditionalRenderState().setWireframe(true);
		geom.setMaterial(mat);
		geom2.setMaterial(mat);
		geom3.setMaterial(mat);
		geom2.setLocalTranslation(0, (WorldCube.size) * WorldCube.width, 0);
		geom3.setLocalTranslation(0, 0, (WorldCube.size) * WorldCube.width);
		world.attachChild(geom);
		world.attachChild(geom2);
		world.attachChild(geom3);
		
		RigidBodyControl geom_phy = new RigidBodyControl(test.getCosShape(), 0.0f);
		geom.addControl(geom_phy);
		bulletAppState.getPhysicsSpace().add(geom_phy);
		RigidBodyControl geom_phy2 = new RigidBodyControl(test2.getCosShape(), 0.0f);
		geom2.addControl(geom_phy2);
		bulletAppState.getPhysicsSpace().add(geom_phy2);
		RigidBodyControl geom_phy3 = new RigidBodyControl(test3.getCosShape(), 0.0f);
		geom3.addControl(geom_phy3);
		bulletAppState.getPhysicsSpace().add(geom_phy3);
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

		mark.setLocalTranslation(0, 0, 0);
		rootNode.attachChild(mark);
		rootNode.attachChild(world);

	}
	private void initMouse() {
		inputManager.addMapping("Shoot", new MouseButtonTrigger(MouseInput.BUTTON_LEFT)); // trigger 2: left-button click
		inputManager.addListener(actionListener, "Shoot");
	}
	/** Defining the "Shoot" action: Determine what was hit and how to respond. */
	private ActionListener actionListener = new ActionListener() {

		public void onAction(String name, boolean keyPressed, float tpf) {
			if (name.equals("Shoot") && !keyPressed) {
				// 1. Reset results list.
				CollisionResults results = new CollisionResults();
				// 2. Aim the ray from cam loc to cam direction.
				Ray ray = new Ray(cam.getLocation(), cam.getDirection());
				// 3. Collect intersections between Ray and Shootables in results list.
				world.collideWith(ray, results);
				// 4. Print the results
				System.out.println("----- Collisions? " + results.size() + "-----");
				for (int i = 0; i < results.size(); i++) {
					// For each hit, we know distance, impact point, name of geometry.
					float dist = results.getCollision(i).getDistance();
					Vector3f pt = results.getCollision(i).getContactPoint();
					String hit = results.getCollision(i).getGeometry().getName();
					System.out.println("* Collision #" + i);
					System.out.println("  You shot " + hit + " at " + pt + ", " + dist + " wu away.");
				}
				// 5. Use the results (we mark the hit object)
				if (results.size() > 0) {
					// The closest collision point is what was truly hit:
					CollisionResult closest = results.getClosestCollision();
					// Let's interact - we mark the hit with a red dot.
					mark.setLocalTranslation(closest.getContactPoint());
					Vector3f hitLoc = closest.getContactPoint();
					System.out.println("   x:" + hitLoc.x + "    y:" + hitLoc.y + "    z:" + hitLoc.z);
					Vector3f hitGeom = closest.getGeometry().getLocalTranslation();
					System.out.println("   x:" + hitGeom.x + "    y:" + hitGeom.y + "    z:" + hitGeom.z);
					Vector3f bInd = hitLoc.subtract(hitGeom);
					WorldCube.getBlockInd(bInd);
					System.out.println("   x:" + bInd.x / 3f + "    y:" + Math.round(bInd.y) + "    z:" + Math.round(bInd.z) );
					rootNode.attachChild(mark);
					Vector3f play = player.getPhysicsLocation();
					System.out.println("Player - x:" + Math.round(play.getX()) + "   y:" + Math.round(play.getY()) + "   z:" + Math.round(play.getZ()));
				} else {
					// No hits? Then remove the red mark.
					rootNode.detachChild(mark);
				}
			}
		}
	};

	public void initFloor() {
		floor_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		floor_mat.setColor("Color", ColorRGBA.DarkGray);
		floor = new Box(Vector3f.ZERO, 100f, 0.1f, 50f);
		// floor.scaleTextureCoordinates(new Vector2f(3, 6));
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
	/** A red ball that marks the last spot that was "hit" by the "shot". */
	protected void initMark() {
		Sphere sphere = new Sphere(30, 30, 0.2f);
		mark = new Geometry("BOOM!", sphere);
		Material mark_mat = new Material(assetManager, "Common/MatDefs/Misc/Unshaded.j3md");
		mark_mat.setColor("Color", ColorRGBA.Red);
		mark.setMaterial(mark_mat);
	}

	/** A centered plus sign to help the player aim. */
	protected void initCrossHairs() {
		// guiNode.detachAllChildren();
		guiFont = assetManager.loadFont("Interface/Fonts/Default.fnt");
		BitmapText ch = new BitmapText(guiFont, false);
		ch.setSize(guiFont.getCharSet().getRenderedSize() * 2);
		ch.setText("+"); // crosshairs
		ch.setLocalTranslation( // center
				settings.getWidth() / 2 - guiFont.getCharSet().getRenderedSize() / 3 * 2, settings.getHeight() / 2 + ch.getLineHeight() / 2, 0);
		guiNode.attachChild(ch);
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

}
