// ��C�Q�[����T�[�o�v���O����UmiServer3.java
// ���̃v���O������,�C�Q�[���̃T�[�o�v���O�����ł��B
//
// Todo:
// �Q�[���X�^�[�g����"Game Start!"��2�b�ԉ�ʂɕ\������悤�ɂ���
// �e�L�X�g�̃v���O�������g�����A
// �G�l���M�[�^���N������ނ���i�_�����قȂ�j�`�ɕύX�B�����B
// �i1�_����4�_�ŏo���p�x�� 25%�A25%�A25%�A25% �Ƃ���j
//
// �g����>java UmiServer3 �Q���l�� �R���Ԋu[ms](def=5000) �v���C����[s](def=180)
//
// �N�������,�|�[�g�ԍ�10000 �Ԃɑ΂���N���C�A���g����̐ڑ���҂��܂�
// �v���O�������~����ɂ̓R���g���[��C ����͂��Ă�������

// ���C�u�����̗��p
import java.io.*;
import java.net.*;
import java.util.*;
import java.awt.*;// �O���t�B�b�N�X

// UmiServer�N���X
// UmiServer3�N���X��,UmiServer�v���O�����̖{�̂ł�
public class UmiServer3 {
	static final int DEFAULT_PORT = 10000;
							//UmiServer�ڑ��p�|�[�g�ԍ�
	static ServerSocket serverSocket;
	static Vector<Socket> connections;
				//�N���C�A���g�Ƃ̃R�l�N�V������ێ�����Vector�I�u�W�F�N�g
	static Vector<int[]> energy_v; // �R���^���N�̈ʒu��񃊃X�g
	static Hashtable<String, Ship> userTable = null;
				// �N���C�A���g�֘A���o�^�p�e�[�u��
	static Random random = null;
				// �R���^���N��ǉ�����X���b�h
	static Thread et;

	static Thread pt; // ����I�ĕ`��^�X�N
				// ���Ԃ��o�߂�����^�C�}�[���^�C�}�[�^�X�N
	static Timer timer;
	static TimerTask ttask;
				// �Q������l��
	static int player_num;
				// ���Z����[s]
	static int play_time = 180;
				// �R���Ԋu[ms]
	static int energy_interval = 5000;
	static int num = 0;	// ���ݎQ�����Ă���l��
				// �Q�[���I���̃u�[���l
	static boolean is_finish = false;
	static int[] scores;
	static String[] names;
	
	// ��ʕ\���p�C���X�^���X
	static Frame f;// �N���C�A���g���\���p�E�B���h�E
	static Panel p;// �㉺���E�̈ړ��{�^���ƊC�̏�Ԃ�\������p�l��
	static Canvas c;// �C�̏�Ԃ�\������L�����o�X

	static Image    imb; // �_�u���o�b�t�@�����O�p
	static Graphics g;  // �_�u���o�b�t�@�����O�p
	static Graphics fg; // foreground

	// addConnection���\�b�h
	// �N���C�A���g�Ƃ̐ڑ���Vector�I�u�W�F�N�gconnections�ɓo�^���܂�
	public static void addConnection(Socket s){
		if (connections == null){//���߂ẴR�l�N�V�����̏ꍇ��,
			connections = new Vector<Socket>();// connections���쐬���܂�
		}
		connections.addElement(s);
	}

	// deleteConnection���\�b�h
	// �N���C�A���g�Ƃ̐ڑ���connections����폜���܂�
	public static void deleteConnection(Socket s){
		if (connections != null){
			connections.removeElement(s);
		}
	}

	// loginUser���\�b�h
	// login�R�}���h�̏����Ƃ���,���p�҂̖��O��D�̈ʒu��o�^���܂�
	public static int loginUser(String name){
		if (userTable == null){// �o�^�p�e�[�u�����Ȃ���΍쐬���܂�
			userTable = new Hashtable<String, Ship>();
		}
		if (random == null){// �����̏��������܂�
			random = new Random();
		}
		
		// ���łɎQ���l���������Ă����狑��
		if( num == player_num ){
			System.out.println("invalid login: No more player can login.");
			System.out.flush();
			return 0;
		}
		
		// ���łɎQ�����Ă��閼�O�͋���
		if( userTable.containsKey(name) ){
			System.out.println("invalid login: "+name+" has already logined.");
			System.out.flush();
			return 0;
		}
		
		// �D�̏����ʒu�𗐐��Ō��肵�܂�
		int ix = Math.abs(random.nextInt()) % 256;
		int iy = Math.abs(random.nextInt()) % 256;

		// �N���C�A���g�̖��O��D�̈ʒu��\�ɓo�^���܂�
		userTable.put(name, new Ship(ix, iy));
		// �T�[�o���̉�ʂɂ��N���C�A���g�̖��O��\�����܂�
		//System.out.println(""+num);
		System.out.println("login:" + name);
		System.out.flush();
		
		num++;
		
		// �Q���l���ɒB������J�n
		if( num == player_num ){
			et.start();
			timer = new Timer();
			timer.schedule(ttask, 1000, 1000);
			System.out.println("Game start!!");
			System.out.flush();
		}
		return 1;
	}

	// logoutUser���\�b�h
	// �N���C�A���g�̃��O�A�E�g���������܂�
	public static void logoutUser(String name){
		// �T�[�o����ʂɃ��O�A�E�g����N���C�A���g�̖��O��\�����܂�
		System.out.println("logout:" + name);
		System.out.flush();
		// �o�^�p�e�[�u�����獀�ڂ��폜���܂�
		userTable.remove(name);
	}

	// left���\�b�h
	// �������̑D�����ɓ�������,�R���^���N���E���邩�ǂ������肵�܂�
	// ����ɂ�calculation���\�b�h���g���܂�
	public static void left(String name){
		Ship ship = (Ship) userTable.get(name);
		ship.left();
		calculation();
	}

	// right���\�b�h
	// �������̑D���E�ɓ�������,�R���^���N���E���邩�ǂ������肵�܂�
	// ����ɂ�calculation���\�b�h���g���܂�
	public static void right(String name){
		Ship ship = (Ship) userTable.get(name);
		ship.right();
		calculation();
	}

	// up���\�b�h
	// �������̑D����ɓ�������,�R���^���N���E���邩�ǂ������肵�܂�
	// ����ɂ�calculation���\�b�h���g���܂�
	public static void up(String name){
		Ship ship = (Ship) userTable.get(name);
		ship.up();
		calculation();
	}

	// down���\�b�h
	// �������̑D�����ɓ�������,�R���^���N���E���邩�ǂ������肵�܂�
	// ����ɂ�calculation���\�b�h���g���܂�
	public static void down(String name){
		Ship ship = (Ship) userTable.get(name);
		ship.down();
		calculation();
	}

	// calculation���\�b�h
	// �R���^���N�ƑD�̈ʒu�֌W�𒲂ׂ�,�R���^���N���E���邩�ǂ������肵�܂�
	static void calculation(){
		if (userTable != null && energy_v != null){
			// ���ׂẴN���C�A���g�ɂ��Ĕ��肵�܂�
			for (Enumeration users = userTable.keys();
				 users.hasMoreElements();) {
				// ���肷��N���C�A���g�̖��O�ƑD�̈ʒu�����o���܂�
				String user = users.nextElement().toString();
				Ship ship = (Ship) userTable.get(user);
				// �R���^���N���ׂĂɂ���,�D�Ƃ̈ʒu�֌W�𒲂ׂ܂�
				for (Enumeration energys = energy_v.elements();
					 energys.hasMoreElements();) {
					// �R���^���N�̈ʒu�ƑD�̈ʒu�𒲂�,�������v�Z���܂�
					try {
						if (energys.hasMoreElements()) {
							int[] e = (int []) energys.nextElement();
							int x = e[0] - ship.x;
							int y = e[1] - ship.y;
							// �C�̕��ł̋����v�Z�𐳂�������
							if (x >  128) x -= 256;
							if (x < -128) x += 256;
							if (y >  128) y -= 256;
							if (y < -128) y += 256;
							// double r = Math.sqrt(x * x + y * y);
							// ����"10"���߂��Ȃ�R���^���N����荞�݂܂�
							if (x*x+y*y < 100){ // if (r < 10)
								ship.point += e[2]; 
								energy_v.removeElement(e);
							}
						}
					} catch (Exception err) {
						System.out.println("error in calculation:" + err);
					}
				}
			}
		}
	}

	// statInfo���\�b�h
	// STAT�R�}���h���������܂�
	// �N���C�A���g�ɑ΂���,�D�̏��(ship_info)��,
	// �C���Y�����Ă���R���^���N�̏���(energy_info)�𑗐M���܂�
	public static void statInfo(PrintWriter pw){
		// �D�̏��(ship_info)�̑��M
		pw.println("ship_info");
		if (userTable != null){
			for (Enumeration users = userTable.keys();
				 users.hasMoreElements();) {
				String user = users.nextElement().toString();
				Ship ship = (Ship) userTable.get(user);
				pw.println(user + " " + ship.x + " "
								+ ship.y + " " + ship.point);
			}
		}
		pw.println(".");// ship_info�̏I��
		// �R���^���N�̏��ienergy_info�j�̑��M
		pw.println("energy_info");
		if (energy_v != null){
			// ���ׂĂ̔R���^���N�̈ʒu�����N���C�A���g�ɑ��M���܂�
			try {
				for (Enumeration energys = energy_v.elements();
					 energys.hasMoreElements();) {
					int[] e = (int []) energys.nextElement();
					pw.println(e[0] + " " + e[1] + " " + e[2]);
				//	System.out.println(e[0] + " " + e[1] + " " + e[2]);
				}
			} catch (Exception err){
				System.out.println("statInfo error: "+err);
			}
		}
		pw.println(".");// enegy_info�̏I��
		pw.flush();
	}
	
	// statInfo_dummy���\�b�h
	// �ᔽ��STAT�R�}���h���������܂�
	// ���0�ł�
	public static void statInfo_dummy(PrintWriter pw){
		pw.println("ship_info");
		pw.println(".");
		pw.println("energy_info");
		pw.println(".");
		pw.flush();
	}

	// putEnergy���\�b�h
	// �R���^���N���P����,�C��Ƀ����_���ɔz�u���܂�
	public static void putEnergy(){
		if (energy_v == null){// ���߂Ĕz�u����ꍇ�̏���
			energy_v = new Vector<int[]>();
		}
		if (random == null){// ���߂ė������g���ꍇ�̏���
			random = new Random();
		}
		// �����ňʒu�����߂ĊC��ɔz�u���܂�
		int[] e = new int[3];
		e[0] = Math.abs(random.nextInt()) % 256;
		e[1] = Math.abs(random.nextInt()) % 256;
		int tmpval = Math.abs(random.nextInt()) % 8;
		// �G�l���M�[�^���N�̓_�� 1����4�_�B1�_25%�A2�_25%�A3�_25%�A4�_25%
		if      (tmpval < 2) e[2] = 1;
		else if (tmpval < 4) e[2] = 2;
		else if (tmpval < 6) e[2] = 3;
		else                 e[2] = 4; // �G�l���M�[�^���N�̓_��
		energy_v.addElement(e);
	}
	
	// finish���\�b�h
	public static void finish(){
		int i,j;
		scores = new int[player_num];
		names = new String[player_num];
		
		// ���Ҕ���
		i = 0;
		for( Enumeration e = userTable.keys(); e.hasMoreElements(); i++){
			String name = e.nextElement().toString();
			Ship ship = (Ship)userTable.get(name);
			
			scores[i] = ship.point;
			names[i] = name;
		}
		
		int tmp;
		String tmp2;
		
		// �\�[�g
		for(i=0; i<player_num-1; i++){
			for(j=i+1; j<player_num; j++){
				if( scores[j] > scores[i] ){
					tmp = scores[i];	scores[i] = scores[j];	scores[j] = tmp;
					tmp2 = names[i];	names[i] = names[j];	names[j] = tmp2;
				}
			}
		}
		
		for(i=0; i<player_num; i++){
			System.out.println(""+(i+1)+"�ʁF"+names[i]+", "+scores[i]+"�_");
		}
		
		is_finish = true;
		
		paint();
	}
	
	public static void repaint() {
		fg.drawImage(imb,0,0,null);
	}

	// paint���\�b�h
	// ���݂̏󋵂�\�����܂�
	public static void paint()
	{
		int x,y;
//		Graphics g = c.getGraphics();
		Enumeration e;
		String name;
		
		// �t�H���g�T�C�Y��2�{��
		Font font     = new Font(null, Font.PLAIN, 20);
		Font fontmini = new Font(null, Font.PLAIN, 16);
		g.setFont(font);
		
		// �C�̕`��(�P�Ȃ���l�p�`�ł�)
		g.setColor(Color.blue);
//		g.fillRect(0, 20, 512, 512);
		g.fillRect(0, 20, 532, 580);

		// �R���̕\��
		if( energy_v != null ){
			try {
				for( e = energy_v.elements(); e.hasMoreElements(); ){
					int[] p = (int [])e.nextElement();
					x = p[0]*2;
					y = p[1]*2;
					
					// �R���^���N��,�������̐ԊۂŎ����܂�
					g.setColor(Color.red);
					g.fillOval(x - 10, 532 - y - 10, 20, 20);
					g.setColor(Color.white);
					g.fillOval(x - 6, 532 - y - 6, 12, 12);
					g.setColor(Color.black);
					g.setFont(fontmini);
					g.drawString(""+p[2], x-6+2, 532-y+6);
				}
			} catch (Exception err) {
				System.out.println("error in paint:" + err);
			}
		}
		
		// �D�̕`��
		if( userTable != null ){
			g.setFont(font);
			for( e = userTable.keys(); e.hasMoreElements(); ){
				name = e.nextElement().toString();
				Ship ship = (Ship) userTable.get(name);
				x = ship.x*2;
				y = ship.y*2;
				
				// �e���ɂ���i���ň��`��j
				// �D��\�����܂�
				g.setColor(Color.black);
				g.fillOval(x - 19, 532 - y - 19, 40, 40);
				if (x < 40)     g.fillOval(532+x-19,532-y-19,40,40);
				if (x > 532-40) g.fillOval(-532+x-19,532-y-19,40,40);
				if (y < 40)     g.fillOval(x-19,-y-19,40,40);
				if (y > 532-40) g.fillOval(x-19,1064-y-19,40,40);
				// ���_��D�̉E���ɕ\�����܂�
				g.drawString(""+ship.point, x + 21, 532 - y + 19) ;
				if (x > 532-60) g.drawString(""+ship.point,-532+x+21, 532-y+19);
				if (y < 40) g.drawString(""+ship.point,x+21, -y+19);
				if (x>532-60&&y<40) g.drawString(""+ship.point,-532+x+21,-y+19);
				// ���O��D�̉E��ɕ\�����܂�
				g.drawString(name, x+21, 532-y-19) ;
				if (x > 532-80) g.drawString(name, -532+x+21, 532-y-19);
				if (y > 532-40) g.drawString(name, x+21, 1064-y-19);
				if (x>532-80&&y>532-40) g.drawString(name, -532+x+21, 1064-y-19);

				// �D��\�����܂�
				g.setColor(Color.green);
				g.fillOval(x - 20, 532 - y - 20, 40, 40);
				if (x < 40)     g.fillOval(532+x-20,532-y-20,40,40);
				if (x > 532-40) g.fillOval(-532+x-20,532-y-20,40,40);
				if (y < 40)     g.fillOval(x-20,-y-20,40,40);
				if (y > 532-40) g.fillOval(x-20,1064-y-20,40,40);
				// ���_��D�̉E���ɕ\�����܂�
				g.drawString(""+ship.point, x + 20, 532 - y + 20) ;
				if (x > 532-60) g.drawString(""+ship.point,-532+x+20, 532-y+20);
				if (y < 40) g.drawString(""+ship.point,x+20, -y+20);
				if (x>532-60&&y<40) g.drawString(""+ship.point,-532+x+20,-y+20);
				// ���O��D�̉E��ɕ\�����܂�
				g.drawString(name, x+20, 532-y-20) ;
				if (x > 532-80) g.drawString(name, -532+x+20, 532-y-20);
				if (y > 532-40) g.drawString(name, x+20, 1064-y-20);
				if (x>532-80&&y>532-40) g.drawString(name, -532+x+20, 1064-y-20);
			}
		}
			
		// ���\���G���A�̕`��
		g.setColor(Color.black);
		g.fillRect(0, 0, 532, 20);
		
		// ���̕`��
		g.setColor(Color.white);
		g.drawString("�c�莞�ԁF"+play_time, 0, 20);

		// �N�����O�C�����Ă��Ȃ��Ƃ��́A�J�n�O���
		if( num == 0 ) {
			String str = "Umi Game";
			font = new Font(null, Font.PLAIN, 60);
			g.setFont(font);
			g.setColor(Color.green);
			int width = g.getFontMetrics().stringWidth(str);
			g.drawString(str, 256-width/2, 256-15);
		}
		
		// ���҂̕\��
		if( is_finish && scores.length > 1){
			String str;
			
			if( scores[0] == scores[1] ){
				str = "DRAW !!";
			} else {
				str = names[0] + " WIN !!";
			}
			font = new Font(null, Font.PLAIN, 60);
			g.setFont(font);
			g.setColor(Color.red);
			int width = g.getFontMetrics().stringWidth(str);
			g.drawString(str, 256-width/2, 256-15);
		}

		fg.drawImage(imb,0,0,null);
	}

	// main���\�b�h
	// �T�[�o�\�P�b�g�̍쐬�ƃN���C�A���g�ڑ��̏���
	// ����ѓK���ȃ^�C�~���O�ł̔R���^���N�̒����ǉ��������s���܂�
	public static void main(String[] args){
		try {// �T�[�o�\�P�b�g�̍쐬
			serverSocket = new ServerSocket(DEFAULT_PORT);
		}catch (IOException e){
			System.err.println("can't create server socket.");
			System.exit(1);
		}
		
		// ��������
		switch( args.length ){
			case 3:	play_time = Integer.parseInt(args[2]);
			case 2:	energy_interval = Integer.parseInt(args[1]);
			case 1:	player_num = Integer.parseInt(args[0]);
				break;
			default:
				System.err.println("usage:\njava UmiServer player_num [energy_interval(ms)] [play_time(s)]");
				System.exit(1);
				break;
		}


		pt = new Thread() { // ����I�ɍĕ`�悷��^�X�N�����܂��B
			public void run() {
				while(true){
					try {
						sleep(10);
					} catch (InterruptedException e) {
						break;
					}
					UmiServer3.paint();

//					if (is_finish) {
//						break;
//					}
				}
			}
		};

		
		// �R���^���N�����ɒǉ�����X���b�het�����܂�
		et = new Thread(){
			public void run(){
				while(true){
					try {
						// �X���b�het���x�~�����܂�
						sleep(UmiServer3.energy_interval);
					}catch(InterruptedException e){
						break;
					}
					// �C��ɂP�R���^���N��z�u���܂�
					if( UmiServer3.play_time > 0 ){
						UmiServer3.putEnergy();
//						UmiServer3.paint();
					} else
						break;
				}
			}
		};
		
		// ���Ԃ��o�߂�����^�C�}�[�^�X�Nttask�����܂�
		ttask = new TimerTask(){
			public void run(){
				UmiServer3.play_time--;
//				UmiServer3.paint();
				
				// �I������
				if( UmiServer3.play_time == 0 ){
					UmiServer3.timer.cancel();
					UmiServer3.finish();
				}
			}
		};
		
		f = new Frame();//�N���C�A���g���E�B���h�E�S�̂̕\��
		p = new Panel();//�C�\�������Ƒ���{�^���̕\��
		p.setLayout(new BorderLayout());
		
		// �C��̗l�q��\������Canvas���쐬���܂�
		c = new Canvas();
		c.setSize(532, 600);	// �傫���̐ݒ�
		// �t���[���ɕK�v�ȕ��i�����t���܂�
		p.add(c);
		f.add(p);

		// �t���[��f��\�����܂�
		f.setSize(532, 600);
		f.setVisible(true);

		imb = p.createImage(532,600); 
		g = imb.getGraphics();
		g.setColor(Color.blue);
		g.fillRect(0, 20, 532, 580);
		g.setColor(Color.black);
		g.fillRect(0, 0, 532, 20);

		fg = c.getGraphics();
		fg.drawImage(imb,0,0,null);
		
//		paint();
		pt.start(); // ����`��^�X�N
		
		// �\�P�b�g�̎�t��,�N���C�A���g�����v���O�����̊J�n�������s���܂�
		while (true) {// �������[�v
			try {
				Socket cs = serverSocket.accept();
				addConnection(cs);// �R�l�N�V������o�^���܂�
				// �N���C�A���g�����X���b�h���쐬���܂�
				Thread ct = new Thread(new clientProc(cs));
				ct.start();
			}catch (IOException e){
				System.err.println("client socket or accept error.");
			}
		}
	}
}

// clientProc�N���X
// clientProc�N���X��,�N���C�A���g�����X���b�h�̂ЂȌ`�ł�
class clientProc implements Runnable {
	Socket s; // �N���C�A���g�ڑ��p�\�P�b�g
	// ���o�̓X�g���[��
	BufferedReader in;
	PrintWriter out;
	String name = null;// �N���C�A���g�̖��O
	Date date;
	long	lasttime = 0;	// �Ō��stat�R�}���h�𑗂�������
	int		command=2;		// �R�}���h��

	// �R���X�g���N�^clientProc
	// �\�P�b�g���g���ē��o�̓X�g���[�����쐬���܂�
	public clientProc(Socket s) throws IOException {
		this.s = s;
		in = new BufferedReader(new InputStreamReader(
					s.getInputStream()),8192);
		out = new PrintWriter(s.getOutputStream());
	}

	// run���\�b�h
	// �N���C�A���g�����X���b�h�̖{�̂ł�
	public void run(){
		try {
			//LOGOUT�R�}���h��M���Q�[���I���܂ŌJ��Ԃ��܂�
			while ( true ) {
				// �N���C�A���g����̓��͂�ǂݎ��܂�
				String line = in.readLine();
				if (line == null) {
					System.out.println("null");
					continue;
				}
// if (name != null) System.out.println(name+"["+line+"]");

				// name����̏ꍇ�ɂ�LOGIN�R�}���h�݂̂��󂯕t���܂�
				if (name == null){
					StringTokenizer st = new StringTokenizer(line);
					String cmd = st.nextToken();
					if ("login".equalsIgnoreCase(cmd)){
						name = st.nextToken();
						if (UmiServer3.loginUser(name) == 0) break;
						// ���O�C���ł��Ȃ�������N���C�A���g�Ƃ̐ڑ��I��
					}else{
						// LOGIN�R�}���h�ȊO��,���ׂĖ������܂�
						continue;
					}
					
				// �Q�[���I��������LOGOUT�R�}���h�݂̂��󂯕t���܂�
				}else if( UmiServer3.is_finish ){
					StringTokenizer st = new StringTokenizer(line);
					String cmd = st.nextToken();
					if( cmd.equalsIgnoreCase("LOGOUT") ){
						UmiServer3.logoutUser(name);
						break;
					}
				}else{
					// name����łȂ��ꍇ�̓��O�C���ς݂ł�����,�R�}���h���󂯕t���܂�
					StringTokenizer st = new StringTokenizer(line);
					String cmd = st.nextToken();// �R�}���h�̎��o��
					// �R�}���h�𒲂�,�Ή����鏈�����s���܂�
					if ("STAT".equalsIgnoreCase(cmd)){
						// 500�~���b�o���Ă���Ύ󂯕t����
						date = new Date();
						if( date.getTime() - lasttime >= 500 ){
							UmiServer3.statInfo(out);
							lasttime = date.getTime();	// �����擾
							command = 2;
						} else {
							UmiServer3.statInfo_dummy(out);
						}
					} else if ("LOGOUT".equalsIgnoreCase(cmd)){
						UmiServer3.logoutUser(name);
						// LOGOUT�R�}���h�̏ꍇ�ɂ͌J��Ԃ����I�����܂�
						break;
					} else {
						// �R�}���h�񐔐���
						if( command > 0 ){
							if ("UP".equalsIgnoreCase(cmd)){
								UmiServer3.up(name);
							} else if ("DOWN".equalsIgnoreCase(cmd)){
								UmiServer3.down(name);
							} else if ("LEFT".equalsIgnoreCase(cmd)){
								UmiServer3.left(name);
							} else if ("RIGHT".equalsIgnoreCase(cmd)){
								UmiServer3.right(name);
							} else {
								continue;
							}
							command--;
						} else {
							continue;
						}
					}
				}
				
//				UmiServer3.paint();
			}
			// �o�^�����폜��,�ڑ���ؒf���܂�
			UmiServer3.deleteConnection(s);
			s.close();
		}catch (IOException e){
			try {
				s.close();
				if (name != null ) System.out.println(name+" "+e+"(IOEXCEPTION)");
			}catch (IOException e2){
				if (name != null ) System.out.println(name+" "+e2+"(IOEXCEPTION) e2");
			}
		}
	}
}

// Ship�N���X
// �D�̈ʒu��,�l�������R���^���N�̐����Ǘ����܂�
class Ship {
	// �D�̈ʒu���W
	int x;
	int y;
	// �l�������R���^���N�̌�
	int point = 0;

	// �R���X�g���N�^
	// �����ʒu���Z�b�g���܂�
	public Ship(int x, int y){
		this.x = x;
		this.y = y;
	}

	// left���\�b�h
	// �D�����ɓ������܂�
	public void left(){
		x -= 10;
		// ���̕ӂ͉E�̕ӂɂȂ����Ă��܂�
		if (x < 0)
			x += 256;
	}

	// right���\�b�h
	// �D���E�ɓ������܂�
	public void right(){
		x += 10;
		// �E�̕ӂ͍��̕ӂɂȂ����Ă��܂�
		x %= 256;
	}

	// up���\�b�h
	// �D����ɓ������܂�
	public void up(){
		y += 10;
		// ��̕ӂ͉��̕ӂɂȂ����Ă��܂�
		y %= 256;
	}

	// down���\�b�h
	// �D�����ɓ������܂�
	public void down(){
		y -= 10;
		// ���̕ӂ͏�̕ӂɂȂ����Ă��܂�
		if (y < 0)
			y += 256;
	}
}