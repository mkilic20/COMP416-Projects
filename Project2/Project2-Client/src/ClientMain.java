import java.io.IOException;
import java.util.Arrays;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.data.category.DefaultCategoryDataset;
import javax.swing.JFrame;

public class ClientMain {
    public static void main(String[] args) throws IOException {
    	int TCPPORT = 6666;
    	int SSLPORT = 5555;
    	int KUID = 76621;
    	
        // For SSL connection
        String[] responseList;
        String[] delayListSSL = new String[5];
        String[] delayListTCP = new String[5];
        for (int i=0; i<5; i++) {
        	try {
    			ConnectToServer sslClient = new ConnectToServer("localhost", SSLPORT, TCPPORT, true);
    			sslClient.Connect();
    			responseList = sslClient.SendForAnswer(Integer.toString(KUID));
    			System.out.println(responseList[0]);
    			delayListSSL[i] = responseList[1];
    			sslClient.Disconnect();
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
            // For TCP connection
            try {
    			ConnectToServer tcpClient = new ConnectToServer("localhost", SSLPORT, TCPPORT, false);
    			tcpClient.Connect();
    			responseList = tcpClient.SendForAnswer(Integer.toString(KUID));
    			System.out.println(responseList[0]);
    			delayListTCP[i]= responseList[1];
    			tcpClient.Disconnect();
    		} catch (Exception e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
        }
		System.out.println(Arrays.toString(delayListSSL));
		System.out.println(Arrays.toString(delayListTCP));
		
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
        for (int i = 0; i < 5; i++) {
            dataset.addValue(Long.valueOf(delayListTCP[i]), "TCP", Integer.toString(i + 1));
            dataset.addValue(Long.valueOf(delayListSSL[i]), "SSL", Integer.toString(i + 1));
        }

        JFreeChart lineChart = ChartFactory.createLineChart("TCP/SSL Communication Delays Line Chart","Execution Number", "Delay (ms)", dataset);

        ChartPanel chartPanel = new ChartPanel(lineChart);
        JFrame frame = new JFrame();
        frame.setContentPane(chartPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        
    }
}