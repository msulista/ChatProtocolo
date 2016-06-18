
package chatsocketprotocol;

import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import javax.swing.JButton;
import javax.swing.JFrame;
import static javax.swing.JFrame.EXIT_ON_CLOSE;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;


public class ChatClient extends JFrame{
   // "192.168.1.114"
    private final String ENDERECO = "localhost";
    private final int PORTA = 6789;    
	
	private String nomeUsuario;
	private JTextField textoEnviar;
	private JTextArea textoRecebido;
	private JButton botaoEnviar;
	private PrintWriter escritor;
	private Scanner leitor;
	
	public ChatClient (String nome){
		super("Chat do "+nome);
		this.nomeUsuario = nome;
		this.setDefaultCloseOperation(EXIT_ON_CLOSE);
		this.setBounds(100,100,400,400);
		
		iniciaComponentes();
		
		iniciaConexao();
		
		this.setVisible(true);	
	}


	private void iniciaComponentes() {
		JPanel painelEnvio = new JPanel(new BorderLayout());
		textoEnviar  = new JTextField();
		textoEnviar.addActionListener(new EnviaMensagem());
		painelEnvio.add(textoEnviar, BorderLayout.CENTER);	
		botaoEnviar  = new JButton("Enviar");
		botaoEnviar.addActionListener(new EnviaMensagem());
		painelEnvio.add(botaoEnviar, BorderLayout.EAST);	
		this.add(painelEnvio, BorderLayout.NORTH);
		
		textoRecebido = new JTextArea();
		textoRecebido.setEditable(false);
		JScrollPane scroll = new JScrollPane(textoRecebido);
		this.add(scroll, BorderLayout.CENTER);
		
	}
	private void iniciaConexao() {
		try {
			
			Socket socket = new Socket(ENDERECO, PORTA);
			escritor = new PrintWriter(socket.getOutputStream());
			leitor = new Scanner(socket.getInputStream());
			new Thread(new RecebeServidor()).start();
			
		} catch (UnknownHostException e) {
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private class RecebeServidor implements Runnable{

		@Override
		public void run() {
			String texto;
			while((texto = leitor.nextLine())!=null)
			{
                            String  mensagem = entregaMensagemDestinatario(texto);
                            textoRecebido.append(mensagem + "\n");
			}
		}
		
	}
	        
	private class EnviaMensagem implements ActionListener{
            
		@Override
		public void actionPerformed(ActionEvent e) {
			escritor.println(nomeUsuario +" - " + formataDataEnvio() + ": "+textoEnviar.getText());
			escritor.flush();
			textoEnviar.setText("");
			textoEnviar.setRequestFocusEnabled(true);
		}
		
	}
	
	public static void main(String args[]){
		new ChatClient(JOptionPane.showInputDialog("Digite o nome:"));
	}
        
        private String formataDataEnvio() {
            Date date = new Date();
            DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy hh:mm");
            String today = formatter.format(date);
            return today;
        }
                  
        private String vericaDestinatario(String mensagem) {
            
            String destinatario = "@All";
            try {
                Scanner scanner = new Scanner(mensagem);
                scanner.useDelimiter("([^<>])"); 
                while (scanner.hasNext()) {
                    destinatario = scanner.next();                    
                }
                scanner.close(); 
                return destinatario;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return null;
        }       
               
        private String entregaMensagemDestinatario(String mensagem) {
            
            String retorno = "";
            String nome = this.vericaDestinatario(mensagem);
            
            if(nome.equalsIgnoreCase(nomeUsuario) || nome.equalsIgnoreCase("All")) {
                retorno = mensagem;
            }
            return retorno;
        }                   

}

