// ��C�Q�[����N���C�A���g�v���O����Robot.java
// ���Z��pUmiServer3.java �Ή���
// ���̃v���O������,�C�Q�[���̃N���C�A���g�v���O�����ł�
// ���߂�ꂽ�菇�ŊC�Q�[�����v���C���܂�
// �g����java Robot �ڑ���T�[�o�A�h���X�Q�[���Q���Җ�
// �N����,�w�肵���T�[�o�Ɛڑ���,�����I�ɃQ�[�����s���܂�
// �N����,�w��񐔂̌J��Ԃ��̌�,logout���܂�
// ���̃v���O������logout�R�}���h������܂���
// �v���O������r���Œ�~����ɂ�,�ȉ��̎菇�𓥂�ł�������
// �i�P�j�R���g���[��C ����͂���Robot�v���O�������~���܂�
// �i�Q�jT1.java�v���O�����Ȃ�,�ʂ̃N���C�A���g���g����Robot�Ɠ������O��login���܂�
// �i�R�jlogout���܂�
// �ʃN���C�A���g�����logout��Ƃ��ȗ������,�T�[�o��ɏ�񂪎c���Ă��܂��܂�

// ���C�u�����̗��p
import java.net.*;// �l�b�g���[�N�֘A
import java.io.*;
import java.util.*;

// Robot�N���X
public class myRobot {
	// ���{�b�g�̓���^�C�~���O���K�肷��ϐ�sleeptime
	int sleeptime = 5 ;/*100ms*/
	// ���{�b�g��logout����܂ł̎��Ԃ��K�肷��ϐ�timeTolive
	int timeTolive = 50 ;/*100ms*/

	int i = 0 ;

	//�{�D�̈ʒu���W��ۑ�
	int x;
	int y;
	//�G�D�̈ʒu���W��ۑ�(20�ǂ܂�)
	int[] Emx = new int[20];
	int[] Emy = new int[20];
	//�R���^���N�̈ʒu���W��ۑ�(200�܂�)
	int[] Egx = new int[200];
	int[] Egy = new int[200];
	int[] Egp = new int[200];

	for(i = 0; i<20; i++){
		Emx[i] = Emy[i] = 512;
	}
	for(i = 0; i<200; i++){
		Egx[i] = Egy[i] = Egp[i] = 512;//�I��������512�Ƃ���
	}

	String line;
	// �R���X�g���N�^
	public Robot2 (String[] args)
	{
		login(args[0],args[1]) ;
		try{
			for(;timeTolive > 0; -- timeTolive){

				/*���������v���암*/

				Thread.sleep(sleeptime * 100);//�K�莞�ԑ҂�
				
				out.println("stat");
				out.flush();

				/*�Ֆʏ��ǂݍ���(UmiClient����ڐA)*/
				String line = in.readLine();// �T�[�o����̓��͂̓ǂݍ���

				//ship_info����n�܂�D�̏��̐擪�s��T���܂�
				while (!"ship_info".equalsIgnoreCase(line))
					line = in.readLine();

				// �e�D�̈ʒu���m�F
				// ship_info�̓s���I�h�݂̂̍s�ŏI���ł�
				line = in.readLine();
				for (int i = 0; !".".equals(line); i++){
					StringTokenizer st = new StringTokenizer(line);
					// ���O��ǂݎ��܂�
					String obj_name = st.nextToken().trim();

					/*obj_name.equals(name)
					���O�������̓o�^�������̂��ǂ������r�ł���*/
					if(obj_name.equals(name)){
						// ���D�̈ʒu���W��ǂݎ��܂�
						x = Integer.parseInt(st.nextToken()) ;
						y = Integer.parseInt(st.nextToken()) ;
					}else{
						//�G�D�̈ʒu���W��ǂݎ��܂�
						Emx[i] = Integer.parseInt(st.nextToken()) ;
						Emy[i] = Integer.parseInt(st.nextToken()) ;
					}
					// ���̂P�s��ǂݎ��܂�
					line = in.readLine();
				}

				// energy_info����n�܂�,�R���^���N�̏���҂��󂯂܂�
				while (!"energy_info".equalsIgnoreCase(line))
					line = in.readLine();

				//�e�R���^���N�̈ʒu���m�F
				// energy_info�̓s���I�h�݂̂̍s�ŏI���ł�
				line = in.readLine();
				for (i = 0; !".".equals(line); i++){
					StringTokenizer st = new StringTokenizer(line);

					// �R���^���N�̈ʒu���W��ǂݎ��܂�
					Egx[i] = Integer.parseInt(st.nextToken()) ;
					Egy[i] = Integer.parseInt(st.nextToken()) ;
					Egp[i] = Integer.parseInt(st.nextToken()) ;

					// ���̂P�s��ǂݎ��܂�
					line = in.readLine();
				}
				/*�����܂ŔՖʓǂݍ���*/

				/*�S�R���̒��ōł��߂����̂�T��*/
				/*�C�̋��E���܂��������͍l���Ă��Ȃ�*/
				int dx,dy,sumd,neari;/*x����,y����,i�Ԗڂ̔R��*/
				dx = dy = 512;
				for(i = 0; Egx[i] < 512; i++){
					if((dx + dy) > Math.abs(x - Egx[i]) + Math.abs(y - Egy[i])){
						dx = Math.abs(x - Egx);
						dy = Math.abs(y - Egy);
						neari = i;
					}
				}
				System.out.println("�ł��߂��R���^���N��(" + Egx[neari] + "," + Egy[neari] + ")�A" + Egp[neari] + "�_\n");
				sumd = dx + dy;
				System.out.println("���̋�����" + sumd + "\n");

				/*���͈�C���̔R���̍��v�_�����v�Z����*/
				/*�C�̋��E���܂����͈͍͂l���Ă��Ȃ�*/
				int sump = 0 ; /*���v�_��*/
				int radius = 50; /*���a*/
				for (i = 0; Egx[i] < 512; i++){
					if(radius >= Math.abs(x - Egx[i]) + Math.abs(y - Egy[i])){
						sump += Egp[i];
					}
				}
				System.out.println("����%d�����̔R���^���N�̍��v�_����" + sump +"\n");

				/*���͈�C���ŁA�e�ی��̂ǂ̕��ʂɑ����̓_�������邩���v�Z����*/
				/*�C�̋��E���܂����͈͍͂l���Ă��Ȃ�*/
				int quadrant1=0,quadrant2=0,quadrant3=0,quadrant4=0;
				int quadrantmax=0,quadrantnum=0;
				for(i = 0; Egx[i] < 512; i++){
					if(radius >= Math.abs(x-Egx[i]) + Math.abs(y - Egy[i])){
						if(Egx[i] >= 0 && Egy[i] >= 0){
							quadrant1 += Egp[i];
						}else if(Egx[i] >= 0 && Egy[i] < 0){
							quadrant2 += Egp[i];
						}else if(Egx[i] < 0 && Egy[i] < 0){
							quadrant3 += Egp[i];
						}else{
							quadrant4 += Egp[i];
						}
					}
				}
				System.out.println("���ی�:" + quadrant1 + "\n���ی�:" + quadrant2 + "\n��O�ی�:" + quadrant3 + "\n��l�ی�:" + quadrant4+ "\n");

				//1vs2�̑傫�����ƁA3vs4�̑傫���ق����ׂ�quadrantnum������
				(quadrant1 > quadrant2) ? quadrantnum = 1: quadrantnum = 2;
				quadrantnum == 1 ? ((quadrant3 > quadrant4) ? ((quadrant3 > quadrant1) ? quadrantnum = 3 : quadrantnum = 1) : ((quadrant4 > quadrant1) ? quadrantnum = 4 : quadrantnum = 1)) : ((quadrant3 > quadrant4) ? ((quadrant3 > quadrant2) ? quadrantnum = 3 : quadrantnum = 2) : ((quadrant4 > quadrant2) ? quadrantnum = 4 : quadrantnum = 2))
				System.out.println("�ł��_���̑����ی��͑�" + quadrantnum + "�ی�\n");
				switch(quadrantnum){
					case 1:
						quadrantmax = quadrant1;
						break;
					case 2:
						quadrantmax = quadrant2;
						break;
					case 3:
						quadrantmax = quadrant3;
						break;
					case 4:
						quadrantmax = quadrant4;
						break;
					default:
						System.out.println("quadrantnum��1~4�ȊO�̒l���w���Ă��܂�\n");
						quadrantmax = 0;
				}
				System.out.println("���v�_��" + quadrantmax + "\n");

				/*���ׂĂ̓G�D�ɂ��Ċe�ی��̓_�����v�Z����*/
				/*�C�̋��E���܂����͈͍͂l���Ă��Ȃ�*/
				for(int j = 0; Emx[j] < 512; j++){
					System.out.println("No." + j + "\n");
					quadrant1=0;
					quadrant2=0;
					quadrant3=0;
					quadrant4=0;
					quadrantmax=0;
					quadrantnum=0;
					for(i = 0; Egx[i] < 512; i++){
						if(radius >= Math.abs(Emx[j]-Egx[i]) + Math.abs(Emy[j] - Egy[i])){
							if(Egx[i] >= 0 && Egy[i] >= 0){
								quadrant1 += Egp[i];
							}else if(Egx[i] >= 0 && Egy[i] < 0){
								quadrant2 += Egp[i];
							}else if(Egx[i] < 0 && Egy[i] < 0){
								quadrant3 += Egp[i];
							}else{
								quadrant4 += Egp[i];
							}
						}
					}
					System.out.println("���ی�:" + quadrant1 + "\n���ی�:" + quadrant2 + "\n��O�ی�:" + quadrant3 + "\n��l�ی�:" + quadrant4+ "\n");

					//1vs2�̑傫�����ƁA3vs4�̑傫���ق����ׂ�quadrantnum������
					(quadrant1 > quadrant2) ? quadrantnum = 1: quadrantnum = 2;
					quadrantnum == 1 ? ((quadrant3 > quadrant4) ? ((quadrant3 > quadrant1) ? quadrantnum = 3 : quadrantnum = 1) : ((quadrant4 > quadrant1) ? quadrantnum = 4 : quadrantnum = 1)) : ((quadrant3 > quadrant4) ? ((quadrant3 > quadrant2) ? quadrantnum = 3 : quadrantnum = 2) : ((quadrant4 > quadrant2) ? quadrantnum = 4 : quadrantnum = 2))
					System.out.println("�ł��_���̑����ی��͑�" + quadrantnum + "�ی�\n");
					switch(quadrantnum){
						case 1:
							quadrantmax = quadrant1;
							break;
						case 2:
							quadrantmax = quadrant2;
							break;
						case 3:
							quadrantmax = quadrant3;
							break;
						case 4:
							quadrantmax = quadrant4;
							break;
						default:
							System.out.println("quadrantnum��1~4�ȊO�̒l���w���Ă��܂�\n");
							quadrantmax = 0;
					}
					System.out.println("���v�_��" + quadrantmax + "\n");
				}


				/*System.out.println("����" + timeTolive + "��") ;
				// 10 ��ɓn��,sleeptime*100�~���b������left�R�}���h�𑗂�܂�
				for(int i = 0;i < 10;++i){
					Thread.sleep(sleeptime * 100) ;
					out.println("left");
					out.println("stat");
					out.flush();
					line = in.readLine();
					while (!".".equals(line)) {
						System.out.println(line);
						line = in.readLine();
					}
					line = in.readLine();
					while (!".".equals(line)) {
						System.out.println(line);
						line = in.readLine();
					}
				}

				// 10 ��ɓn��,sleeptime�b������right�R�}���h�𑗂�܂�
				for(int i = 0;i < 10;++i){
					Thread.sleep(sleeptime * 100) ;
					out.println("right");
					out.println("stat");
					out.flush();
					line = in.readLine();
					while (!".".equals(line)) {
						System.out.println(line);
						line = in.readLine();
					}
					line = in.readLine();
					while (!".".equals(line)) {
						System.out.println(line);
						line = in.readLine();
					}
				}
				// up�R�}���h��1 �񑗂�܂�
				out.println("up");
				out.println("stat");
				out.flush();
				line = in.readLine();
				while (!".".equals(line)) {
					System.out.println(line);
					line = in.readLine();
				}
				line = in.readLine();
				while (!".".equals(line)) {
					System.out.println(line);
					line = in.readLine();
				}*/

				/*�����܂Ŏ�v���암*/
			}

			// logout����
			out.println("logout") ;
			out.flush() ;
			server.close() ;
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}

	// login�֘A�̃I�u�W�F�N�g
	Socket server;// �Q�[���T�[�o�Ƃ̐ڑ��\�P�b�g
	int port = 10000;// �ڑ��|�[�g
	BufferedReader in;// ���̓X�g���[��
	PrintWriter out;// �o�̓X�g���[��
	String name;// �Q�[���Q���҂̖��O

	// login���\�b�h
	// �T�[�o�ւ�login�������s���܂�
	void login(String host, String name){
		try {
			// �T�[�o�Ƃ̐ڑ�
			this.name = name;/*this=���[�J���ϐ���\���ƈ�������ʂ���*/
			server = new Socket(host, port);
			in = new BufferedReader(new InputStreamReader(
			  server.getInputStream()));
			out = new PrintWriter(server.getOutputStream());

			// login�R�}���h�̑��t
			out.println("login " + name);
			out.flush();
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}

	// main���\�b�h
	// Robot���N�����܂�
	public static void main(String[] args){
		new myRobot(args);
	}
}