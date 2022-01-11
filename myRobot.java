// ｢海ゲーム｣クライアントプログラムRobot.java
// 競技会用UmiServer3.java 対応版
// このプログラムは,海ゲームのクライアントプログラムです
// 決められた手順で海ゲームをプレイします
// 使い方java Robot 接続先サーバアドレスゲーム参加者名
// 起動後,指定したサーバと接続し,自動的にゲームを行います
// 起動後,指定回数の繰り返しの後,logoutします
// このプログラムはlogoutコマンドがありません
// プログラムを途中で停止するには,以下の手順を踏んでください
// （１）コントロールC を入力してRobotプログラムを停止します
// （２）T1.javaプログラムなど,別のクライアントを使ってRobotと同じ名前でloginします
// （３）logoutします
// 別クライアントからのlogout作業を省略すると,サーバ上に情報が残ってしまいます

// ライブラリの利用
import java.net.*;// ネットワーク関連
import java.io.*;
import java.util.*;

// Robotクラス
public class myRobot {
	// ロボットの動作タイミングを規定する変数sleeptime
	int sleeptime = 5 ;/*100ms*/
	// ロボットがlogoutするまでの時間を規定する変数timeTolive
	int timeTolive = 50 ;/*100ms*/

	int i = 0 ;

	//本船の位置座標を保存
	int x;
	int y;
	//敵船の位置座標を保存(20隻まで)
	int[] Emx = new int[20];
	int[] Emy = new int[20];
	//燃料タンクの位置座標を保存(200個まで)
	int[] Egx = new int[200];
	int[] Egy = new int[200];
	int[] Egp = new int[200];

	for(i = 0; i<20; i++){
		Emx[i] = Emy[i] = 512;
	}
	for(i = 0; i<200; i++){
		Egx[i] = Egy[i] = Egp[i] = 512;
	}

	String line;
	// コンストラクタ
	public Robot2 (String[] args)
	{
		login(args[0],args[1]) ;
		try{
			for(;timeTolive > 0; -- timeTolive){

				/*ここから主要動作部*/

				Thread.sleep(sleeptime * 100);//規定時間待つ
				
				out.println("stat");
				out.flush();

				/*盤面情報読み込み(UmiClientから移植)*/
				String line = in.readLine();// サーバからの入力の読み込み

				//ship_infoから始まる船の情報の先頭行を探します
				while (!"ship_info".equalsIgnoreCase(line))
					line = in.readLine();

				// 各船の位置を確認
				// ship_infoはピリオドのみの行で終了です
				line = in.readLine();
				for (int i = 0; !".".equals(line); i++){
					StringTokenizer st = new StringTokenizer(line);
					// 名前を読み取ります
					String obj_name = st.nextToken().trim();

					/*obj_name.equals(name)
					名前が自分の登録したものかどうかを比較できる*/
					if(obj_name.equals(name)){
						// 自船の位置座標を読み取ります
						x = Integer.parseInt(st.nextToken()) ;
						y = Integer.parseInt(st.nextToken()) ;
					}else{
						//敵船の位置座標を読み取ります
						ex[i] = Integer.parseInt(st.nextToken()) ;
						ey[i] = Integer.parseInt(st.nextToken()) ;
					}
					// 次の１行を読み取ります
					line = in.readLine();
				}

				// energy_infoから始まる,燃料タンクの情報を待ち受けます
				while (!"energy_info".equalsIgnoreCase(line))
					line = in.readLine();

				//各燃料タンクの位置を確認
				// energy_infoはピリオドのみの行で終了です
				line = in.readLine();
				for (int i = 0; !".".equals(line); i++){
					StringTokenizer st = new StringTokenizer(line);

					// 燃料タンクの位置座標を読み取ります
					Egx[i] = Integer.parseInt(st.nextToken()) ;
					Egy[i] = Integer.parseInt(st.nextToken()) ;
					Egp[i] = Integer.parseInt(st.nextToken()) ;

					// 次の１行を読み取ります
					line = in.readLine();
				}
				/*ここまで盤面読み込み*/

				/*全燃料の中で最も近いものを探す*/
				int dx,dy,neari;
				dx = dy = 512;
				for(int i = 0; Egx[i] < 512; i++){
					if((dx + dy) > Math.abs(x - Egx) + Math.abs(y - Egy)){
						dx = Math.abs(x - Egx);
						dy = Math.abs(y - Egy);
						neari = i;
					}
				}



				/*System.out.println("あと" + timeTolive + "回") ;
				// 10 回に渡り,sleeptime*100ミリ秒おきにleftコマンドを送ります
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

				// 10 回に渡り,sleeptime秒おきにrightコマンドを送ります
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
				// upコマンドを1 回送ります
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

				/*ここまで主要動作部*/
			}

			// logout処理
			out.println("logout") ;
			out.flush() ;
			server.close() ;
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}

	// login関連のオブジェクト
	Socket server;// ゲームサーバとの接続ソケット
	int port = 10000;// 接続ポート
	BufferedReader in;// 入力ストリーム
	PrintWriter out;// 出力ストリーム
	String name;// ゲーム参加者の名前

	// loginメソッド
	// サーバへのlogin処理を行います
	void login(String host, String name){
		try {
			// サーバとの接続
			this.name = name;/*this=ローカル変数や予約語と引数を区別する*/
			server = new Socket(host, port);
			in = new BufferedReader(new InputStreamReader(
			  server.getInputStream()));
			out = new PrintWriter(server.getOutputStream());

			// loginコマンドの送付
			out.println("login " + name);
			out.flush();
		}catch(Exception e){
			e.printStackTrace();
			System.exit(1);
		}
	}

	// mainメソッド
	// Robotを起動します
	public static void main(String[] args){
		new myRobot(args);
	}
}