
import java.io.BufferedReader;

import java.io.InputStreamReader;
import java.util.*;


public class Lexi {
	
	class Token{
		private String _token, chain;
		private int r , c;
		public Token(){ }
		public void setToken(String _token,String chain,int r,int c){
			this._token=_token; this.chain=chain; this.r=r; this.c=c;
		}
		
		public void setToken(String _token,int r,int c){
			this._token=_token; this.r=r; this.c=c;
		}
				
		public String toString(boolean value){
			if(value){
				return "<"+this._token+","+this.r+","+this.c+">";
			}
			return "<"+this._token+","+this.chain+","+this.r+","+this.c+">";
		}
		
	}
	
	public static Lexi st=new Lexi();
	public static Lexi.Token token = st.new Token();
	
	public static String ass;
	public static boolean end_line=false;
	public static boolean findn=false;
	public static int row=0;   //row
	public static int col=1;   //col
	public static int pos_pos;
	public static boolean end_com;
	public static Set<String> mySet =new HashSet<>();  //table
	public static Set<Integer> myCheck = new HashSet<>(); //check left states
        public static Set<Character> sim = new HashSet<>(); // next to integers
	static{   //load Reserved words
		mySet.add("proceso");  mySet.add("entero"); mySet.add("definir"); mySet.add("real"); mySet.add("mientras");
		mySet.add("finsi");mySet.add("finproceso"); mySet.add("escribir");mySet.add("entonces");mySet.add("si");
		mySet.add("verdadero");mySet.add("falso");mySet.add("y");mySet.add("o");mySet.add("mod");mySet.add("no");mySet.add("como");
		mySet.add("logico");
		mySet.add("real");mySet.add("caracter");mySet.add("numerico");mySet.add("mientras");mySet.add("hacer");mySet.add("finmientras");
		mySet.add("algoritmo");mySet.add("finalgoritmo");mySet.add("dimension");mySet.add("leer");mySet.add("limpiar");
		mySet.add("cadena");mySet.add("finfuncion");mySet.add("finsubproceso");mySet.add("pantalla");mySet.add("sino");
		mySet.add("funcion");mySet.add("numero");
		
		mySet.add("texto"); mySet.add("subproceso");mySet.add("segun");mySet.add("finsegun");mySet.add("repetir");mySet.add("hasta");
		mySet.add("que");mySet.add("con");mySet.add("para");mySet.add("paso");mySet.add("hacer");mySet.add("finpara");
		mySet.add("esperar");mySet.add("segundos");mySet.add("milisegundos");mySet.add("de");mySet.add("otro");
		mySet.add("modo");mySet.add("borrar");mySet.add("tecla");mySet.add("esperar");mySet.add("que");
		
        sim.add('=');sim.add('<');sim.add('>');sim.add('+');sim.add('-');sim.add('/');
        sim.add('*');sim.add('%');sim.add(';');sim.add('(');sim.add(')');sim.add('[');sim.add(']');
        sim.add('|');sim.add('&');sim.add('^');sim.add(' ');sim.add('"');sim.add('\'');sim.add(',');
        
       // sim.add('y');sim.add('o');sim.add('n');sim.add('m');      
        //add y,no,mod,o
                
		myCheck.add(8);myCheck.add(19);myCheck.add(16);myCheck.add(5); myCheck.add(36);myCheck.add(9);
	}
	

	

	
	
	public static int solve_dt(int state,char c,String chain,int r){	
		switch(state){
			case 1: 
				if(c==34 || c==39) return 2;   //check token_cadena
				else if( (c>=65 && c<=90) || (c>=97 && c<=122) ) return 4; // token_id
				else if(c>=48 && c<=57) return 6; // token_entero - token_real
				else if(c==126) return 10; // ~
				else if(c==61) return 11; // =
				else if(c==60) return 12; // <- , <> , < , <=
				else if(c==62) return 17; // >, >=
				
				else if(c==43) return 20; // +
				else if(c==45) return 21; // -
				else if(c==47) return 22; // /
				
				else if(c==42) return 23; // *
				else if(c==37) return 24; // %
				else if(c==59) return 25; // ;
				else if(c==58) return 26; // :
				else if(c==40) return 27; // (
				else if(c==41) return 28; // )
				else if(c==91) return 29; // [
				else if(c==93) return 30; // ]
				else if(c==124) return 31; // |
				else if(c==38) return 32; // &
				else if(c==44) return 33; // ,
				else if(c==94) return 34; // ^
				else if(c=='.') return -3;
				
				break;
				
			// <-- begin "token_cadena" -->
			case 2:
				if(c==34 || c==39) return 3;
				else if(c>=32) return 2;  //ASCII characters	
				break;
			case 3:
				token.setToken("token_cadena",chain.substring(1,chain.length()),col,r);
				System.out.println(token.toString(false));
				return -1;
			// <-- end "token_cadena" -->
			
			// <-- begin token_id -->
			case 4:
				if((c>=65 && c<=90) || (c>=97 && c<=122) )return 4; // [A...z,a...z]
				else if(c>=48 && c<=57) return 4; // [0....9]
				else if(c=='_') return 4; // "_"
				else return 5;
				
			case 5:
				if( mySet.contains(chain.toLowerCase()) ){
					if(chain.toLowerCase().equals("y")) ass="token_y";
					else if(chain.toLowerCase().equals("o")) ass="token_o";
					else if(chain.toLowerCase().equals("no")) ass="token_neg";
					else if(chain.toLowerCase().equals("mod")) ass="token_mod";
					else ass=chain.toLowerCase();
					
					token.setToken(ass,col,r);
					System.out.println(token.toString(true));
					return -1;
				}
				else{
					token.setToken("id",chain.toLowerCase(),col,r);
					System.out.println(token.toString(false));
					return -1;
				}
			// <-- end token_id -->
			
			//<-- begin token_entero -->
			case 6:
				if(c>=48 && c<=57) return 6;
				else if(c=='.') return 7;
                else{
                	if(sim.contains(c) || end_line ) return 8;
                	else return -3;
                	}
			//<-- end token_entero -->	
			
			// <--begin token_real -- >
			case 7:
				if(c>=48 && c<=57){
					findn=true;
					return 7;
				}
				else{
					if((sim.contains(c) || end_line) && findn ){
						findn=false;
						return 9;
					}
					else return 37;
				}
			// <-- end token_real -->
			
			case 8: 
				token.setToken("token_entero",chain,col,r);
				System.out.println(token.toString(false));
				return -1;  // accept entero
			case 9: 
				token.setToken("token_real",chain,col,r);
				System.out.println(token.toString(false));
				return -1; //accept real
			
			case 37:
				token.setToken("token_entero",chain.substring(0,chain.length()-1),col,r);
				System.out.println(token.toString(false));
				return -1;  // accept entero
			
			case 10: 
				token.setToken("token_neg",col,r);
				System.out.println(token.toString(true));
				return -1; //accept token_~
			
			case 11:
				token.setToken("token_igual",col,r);
				System.out.println(token.toString(true));
				return -1; //accept token_=
			
			case 12:            //check token_<- , <> , <=, <
				if(c=='-') return 13;
				else if(c=='>') return 14;
				else if(c=='=') return 15;
				else return 16;
			
			
			case 13:
				token.setToken("token_asig",col,r);
				System.out.println(token.toString(true));
				return -1; // accept <-
			
			case 14: 
				token.setToken("token_dif",col,r);
				System.out.println(token.toString(true));
				return -1; // accept <>
			
			case 15:
				token.setToken("token_menor_igual",col,r);
				System.out.println(token.toString(true));
				return -1; // accept <=
			
			case 16:
				token.setToken("token_menor",col,r);
				System.out.println(token.toString(true));
				return -1; //accept <
				
			case 17:           //check token  >= , >
				if(c=='=') return 18;
				else return 19;
				
			case 18:
				token.setToken("token_mayor_igual",col,r);
				System.out.println(token.toString(true));
				return -1; //accept token_>=
			
			case 19: 
				token.setToken("token_mayor",col,r);
				System.out.println(token.toString(true));
				return -1; //accept token >
				
			case 20:
				token.setToken("token_mas",col,r);
				System.out.println(token.toString(true));
				return -1; //accept token_+
			case 21:
				token.setToken("token_menos",col,r);
				System.out.println(token.toString(true));
				return -1; //accept token_-
			case 22:
				if(c=='/') return 35;
				else return 36; // check / or //
				
		
			case 23:
				token.setToken("token_mul",col,r);
				System.out.println(token.toString(true));
				return -1; //accept token_*
			case 24:
				token.setToken("token_mod",col,r);
				System.out.println(token.toString(true));
				return -1; //accept token_%
			case 25:
				token.setToken("token_pyc",col,r);
				System.out.println(token.toString(true));
				return -1; //accept token_;
			case 26:
				token.setToken("token_dosp",col,r);
				System.out.println(token.toString(true));
				return -1; //accept token_:
			case 27:
				token.setToken("token_par_izq",col,r);
				System.out.println(token.toString(true));
				return -1; //accept token_(
			case 28:
				token.setToken("token_par_der",col,r);
				System.out.println(token.toString(true));
				return -1; //accept token_)
			case 29:
				token.setToken("token_cor_izq",col,r);
				System.out.println(token.toString(true));
				return -1; //accept token_[
			case 30:
				token.setToken("token_cor_der",col,r);
				System.out.println(token.toString(true));
				return -1; //accept token_]
			case 31:
				token.setToken("token_o",col,r);
				System.out.println(token.toString(true));
				return -1; //accept token_|
			case 32:
				token.setToken("token_y",col,r);
				System.out.println(token.toString(true));
				return -1; //accept token_&
			case 33:
				token.setToken("token_coma",col,r);
				System.out.println(token.toString(true));
				return -1; //accept token_,
			case 34:
				token.setToken("token_pot",col,r);
				System.out.println(token.toString(true));
				return -1; //accept token_^
				
			case 35:
				return -2; // TAKE CARE OF //
				
			case 36:
				token.setToken("token_div",col,r);
				System.out.println(token.toString(true));
				return -1; //accept token_/
	
			default:
				return 0;
		}
		
		if(c!=' ' && c!=9) return -3;
		return 1;
	}
	

	public static int check(int state,char c,String chain,int r){
		int know=solve_dt(state,c,chain,r);
		if( know==-1) return 1;
		else if(know==-2)return 2;
		return 0;
	}
	
	
	
	
	public static void main(String[] args){
		StringBuilder str=new StringBuilder();
		int aux_state;
		
		try (InputStreamReader instream = new InputStreamReader(System.in);
		     BufferedReader buffer = new BufferedReader(instream)) {
		    
		    String line;
		    while ( ( (line=buffer.readLine() ) != null)  ) {
		    	//if( line.isEmpty() ) continue;
		    	aux_state=1;
		    	boolean seen=false;
		    	boolean checkout=false;
		    	str=new StringBuilder();
		    	end_com=false;
		    	for(int i=0;i<line.length();i++){
		    		
		    		str.append(line.charAt(i));
		    		aux_state=solve_dt(aux_state,line.charAt(i),str.toString(),i );
		    		if( aux_state==-3){
		    			System.out.println(">>> Error lexico (linea: "+col+", posicion: "+(i+2-str.length())+")");
		    			checkout=true;
		    			break;
		    		}
		    		row=i;
		    		//System.out.println("line "+line.charAt(i)+" "+aux_state+" "+str.toString()); 
		    		if(line.charAt(i)=='"' || line.charAt(i)=='\''){
		    			end_com=!end_com;
		    			pos_pos=i;
		    		}
		    		int know=check(aux_state,line.charAt(i),str.substring(0,str.length()-1 ),i+2-str.length() );
		    		
		    		//identify something before de blank line
		    		if( know==1 ){
		    			//System.out.println(str.toString());
		    			//System.out.println("cacho adentro");
		    			if( myCheck.contains(aux_state)){ i--;}
		    			if( aux_state==37){i=i-2;}
		    			aux_state=1;
		    			str=new StringBuilder();
		    		}
		    		
		    		// ignore comments //
		    		else if(know==2){
		    			col++; seen=true;
		    			aux_state=1;
		    			str=new StringBuilder();
		    			break;
		    		}
		    		
		    		// keep initial state
		    		if( aux_state==1)  str=new StringBuilder();
		    		
		    		
		    	}
		    	
		    	if(seen) continue;  //ignore comments
		    	if(end_com){
		    		System.out.println(">>> Error lexico (linea: "+col+", posicion: "+(pos_pos+1)+")");
	    			checkout=true;
		    	}
		    	
		    	if(checkout) break; //break if found a error
		    	
		    	end_line=true;
		    	aux_state=solve_dt(aux_state,'$',str.toString(),row+2-str.length() );
		    	//System.out.println(aux_state);
		    	//System.out.println(str.toString());
		    	if( check(aux_state,'$',str.toString(),row+2-str.length() )==1 ){
	    			//System.out.println("cacho_fuera");
	    			str=new StringBuilder();
	    		}
		    	
		    	
		    	//System.out.println("NEW LINE");
		    	end_line=false;
		    	col++;
		    }
		    
		} catch (Exception e) {
		    e.printStackTrace();
		}
		
	}
}