package orz;
import java.io.*;
import java.util.*;
import java.util.regex.*;
public class onz {
    protected int top, check;
    private int [] dis, path;
    protected int [][] nextnode, weight, prepoint;
    private boolean [] vis;
    private String ALLpath;
    protected String [] pointarray = new String[233];

    private int find_word(String word){
        int flag = -1;
        for (int i = 0; i < top; ++i) if (word.toLowerCase().equals(pointarray[i])) {flag = i; break;}
        return flag;
    }
    private void initialise(){
        nextnode = new int [233][233];
        weight = new int [233][233];
        prepoint = new int [233][233];
        dis = new int [233];
        vis = new boolean [233];
        top = 0;
        for (int i = 0; i < 233; ++i)
            for (int j = 0; j < 233; ++j) { nextnode[i][j] = -1; weight[i][j] = 0;}
    }
    public void readin(String string) throws FileNotFoundException {
        InputStream inputStream = new FileInputStream(string);
        try (Scanner scanner = new Scanner(inputStream, "UTF-8")) {
            string = scanner.useDelimiter("\\A").next();
        }
        string = string.toLowerCase();
        String patt = "\\p{Alpha}+";
        Pattern r = Pattern.compile(patt);
        Matcher m = r.matcher(string);
        initialise();
        String word1, word2 = "";
        while (m.find()) {
            word1 = word2;
            word2 = m.group();
            if (!word1.equals("")) insert_word(word1, word2);
        }
    }
    private void insert_word(String word1, String word2){
        int a, b; check = -1;
	    if (0 != top) a = find_word(word1);
	    else { 
	    	pointarray[0] = word1;
	        ++top;
	        a = 0;
	    }
	    b = find_word(word2);
        if (b == -1) {
            pointarray[top] = word2;
            b = top;
            ++top;
        }
        int t = 0;
        while (nextnode[a][t] != b && nextnode[a][t] != -1) ++t;
        nextnode[a][t] = b;
        ++weight[a][b];
    }
    public void showDirectedGraph(int [][] nexnode, String [] parrray, int total) throws IOException {
        String str = "digraph G {";
        int x1 = -1, x2 = 0, y1 = -1, y2 = nexnode[0][0];
        for (int i = 0; i < total; ++i){
            //if (nexnode[i][0] > -1);
            //str = str + "\n";
            int j = 0;
            while (nexnode[i][j] != -1){
                x2 = i; y2 = nexnode[i][j];
                if (x1 != -1 && y1 != -1){ str = str + parrray[x1] + "->" + parrray[y1] + ",";}
                x1 = x2; y1 = y2; ++j;
            }
        }
        str = str + parrray[x2] + "->" + parrray[y2] + "}";
        String patt = System.getProperty("user.dir");
        File datafile = new File(patt + "/out/Graph.dot");
        try {datafile.createNewFile();}
        catch (IOException e) {}
        try{
            PrintWriter pw = new PrintWriter(datafile);
            pw.write(str);
            pw.close();
        }
        catch (FileNotFoundException e){}
        /*
        Runtime mt = Runtime.getRuntime();
        File myfile = new File(patt+"/release/bin/dot.exe", " -Tjpg "+patt+"/out/Graph.dot -o "+ patt+"/out/Graph.jpg");
        mt.exec(myfile.getAbsolutePath());
        datafile.delete();
        */
    }
    private String getword(String word){
        String patt  = "\\p{Alpha}+";
        Pattern r = Pattern.compile(patt);
        Matcher m = r.matcher(word);
        m.find();
        return m.group();
    }
    public String queryBridgeWords(String word1, String word2){
        String answer;
        word1 = getword(word1); word2 = getword(word2);
        int a = find_word(word1.toLowerCase()), b = find_word(word2.toLowerCase()), num = 0;
        int [] ans = new int [233];
        if (a == -1 || b == -1) answer = "No \"" + word1 + "\" or \"" + word2 + "\" in the graph!";
        else {
            for (int i = 0, k; (k = nextnode[a][i]) != -1; ++i) if (weight[k][b] > 0) ans[num++] = k;
            switch (num){
                case 0: answer = "No bridge word from \"" + word1 + "\" to \"" + word2 + "\"!"; break;
                case 1: answer = "The bridge word from \"" + word1 + "\" to \"" + word2 + "\" is: " + pointarray[ans[0]] + "."; break;
                case 2: answer = "The bridge words from \"" + word1 + "\" to \"" + word2 + "\" are: " + pointarray[ans[0]] + " and " + pointarray[ans[1]] + "."; break;
                default:
                    answer = "The bridge words from \"" + word1 + "\" to \"" + word2 + "\" are : " + pointarray[ans[0]];
                    for (int i = 1; i < num -1; ++i) answer = answer + ", " + pointarray[ans[i]];
                    answer = answer + " and "+ pointarray[ans[num-1]] + "."; break;
            }
        }
        return answer;
    }
    public String generateNewText(String inputText){
    	String patt  = "\\p{Alpha}+";
        Pattern r = Pattern.compile(patt);
        Matcher m = r.matcher(inputText);
        String lastword, newword = "", answer = "";
        int a, b = -1, num;
        int [] ans = new int [233];
        while (m.find()) {
        	lastword = newword; newword = m.group();
        	a = b; b = find_word(newword.toLowerCase());
        	if (a > -1 && b >-1) {
        		answer = answer + lastword + " "; num = 0;
        		int i  = 0;
                while (nextnode[a][i] != -1){
                    int k = nextnode[a][i];
                    if (weight[k][b] > 0) { ans[num] = k; ++num;}
                    ++i;
                }
                long t = System.currentTimeMillis();//获得当前时间的毫秒数
                Random rd = new Random(t);
        		if (num > 0) answer = answer + pointarray[ans[(rd.nextInt() % num + num) % num]] + " ";
        	}
        }
        answer = answer + newword;
        return answer;
    }
    public void dijkstra(int head) {
    	if (check != head) {
    		for (int i = 0; i < top; ++i){
    		    dis[i] = 0x3f3f3f; vis[i] = true;
    		    for (int j = 0; j < top; ++j) prepoint[i][j] = -1;
    		}
    		for (int i = 0; nextnode[head][i] > -1; ++i) {
    		    dis[nextnode[head][i]] = weight[head][nextnode[head][i]];
                prepoint[nextnode[head][i]][0] = head;
            }
            vis[head] = false; dis[head] = 0; prepoint[head][0] = head;
            for (int i = 0; i < top; ++i){
                int x = -1, temp = 0x3f3f3f;
                for (int j = 0; j < top; ++j) if (vis[j] && temp > dis[j]) temp = dis[x = j];
                if (x == -1) break;
                vis[x] = false;
                for (int j = 0; nextnode[x][j] != -1; ++j){
                    int y = nextnode[x][j];
                    if (dis[y] > dis[x] + weight[x][y]) {
                        dis[y] = dis[x] + weight[x][y];
                        for (int k = 1; prepoint[y][k] != -1; ++k) prepoint[y][k] = -1;
                        prepoint[y][0] = x;
                    }
                    else if (dis[y] == dis[x] + weight[x][y]) {
                        int k = 0;
                        while (prepoint[y][k] != -1) ++k;
                        prepoint[y][k] = x;
                    }
                }
            }
    	}
        check = head;
    }
    private void getpath(int k){
        if (prepoint[k][0] == k) {
            for (int i = 0; path[i] != -1; ++i) ALLpath = "->" + pointarray[path[i]] + ALLpath;
            ALLpath = "\n   " + pointarray[k] + ALLpath;
        }
        else {
            int j = 0;
            while (path[j] != -1) ++j;
            path[j] = k;
            for (int i = 0; prepoint[k][i] != -1; ++i) getpath(prepoint[k][i]);
            path[j] = -1;
        }
       return;
    }
    private String calcShortestPath(int a, int b){
        String answer = "";
        if (a<0 || b < 0 || a >= top || b >= top) answer = "Error";
        else {
            dijkstra(a);
            if (dis[b] == 0x3f3f3f) answer = "There is no way from \"" + pointarray[a] + "\" to \"" + pointarray[b] + "\".";
            else {
                ALLpath = "";
                path = new int [233];
                for (int i = 0; i < 233; ++i) path[i] = -1;
                getpath(b);
                answer = "The shortest way form \"" + pointarray[a] + "\" to \"" + pointarray[b] + "\" is:" + ALLpath + ".";
            }
        }
        return answer;
    }
    public String calcShortestPath(String word1, String word2) {
    	String answer;
    	int a = find_word(word1.toLowerCase()), b = find_word(word2.toLowerCase());
    	if (a < 0 || b < 0) answer = "\"" + word1 + "\" or \"" + word2 + "\" is not exist!";
    	else if (a == b) answer = "You input the same words just now.";
    	else answer = calcShortestPath(a, b);
    	return answer;
    }
    public String randomWalk()  {
        boolean [][] vissss = new boolean [top][top];
        for (int i = 0; i < top; ++i) for (int j = 0; j < top; ++j) vissss[i][j] = true;
        long t = System.currentTimeMillis();//获得当前时间的毫秒数
        Random rd = new Random(t);
        int temp = (rd.nextInt() % top + top) % top;
        String answer = pointarray[temp]; int n = 0;
        while (nextnode[temp][n] != -1) ++n;
        if (n != 0){
            int nextn = (rd.nextInt() % n + n) % n;
            while (nextnode[nextn][0] != -1 && vissss[temp][nextn]){
                vissss[temp][nextn] = false;
                temp = nextn; n = 0;
                while (nextnode[temp][n] != -1) ++n;
                nextn = nextnode[temp][(rd.nextInt() % n + n) % n];
                answer = answer + "->" + pointarray[temp];
            }
            answer = answer + "->" + pointarray[nextn];
        }
        String pat = System.getProperty("user.dir");
        pat = pat + "/out/randonwork.txt";
        File datafile = new File(pat);
        try {datafile.createNewFile();}
        catch (IOException e) {}
        try{
            PrintWriter pw = new PrintWriter(datafile);
            pw.write(answer);
            pw.close();
        }
        catch (FileNotFoundException e){}
        return answer;
    }
}