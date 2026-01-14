import java.util.Scanner;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.nio.charset.StandardCharsets;

// Classe base abstrata para as camadas
abstract class Layer {
    protected Layer lowerLayer;
    protected Layer upperLayer;

    public Layer(Layer lower, Layer upper) {
        this.lowerLayer = lower;
        this.upperLayer = upper;
    }

    abstract String send(String data);
    abstract String receive(String data);
}

// Physical Layer
class PhysicalLayer extends Layer {
    public PhysicalLayer(Layer upper) {
        super(null, upper);
    }

    @Override
    String send(String data) {
        System.out.println("Physical: Transmitindo bits...");
        return data;
    }

    @Override
    String receive(String data) {
        System.out.println("Physical: Recebendo bits...");
        return data;
    }
}

// Data Link Layer
class DataLinkLayer extends Layer {
    public DataLinkLayer(Layer lower, Layer upper) {
        super(lower, upper);
    }

    @Override
    String send(String data) {
        String framed = "[FRAME]" + data + "[/FRAME]";
        System.out.println("Data Link: Enquadrando → " + framed);
        return lowerLayer.send(framed);
    }

    @Override
    String receive(String data) {
        String deframed = data.replace("[FRAME]", "").replace("[/FRAME]", "");
        System.out.println("Data Link: Desenquadrando → " + deframed);
        return deframed;
    }
}

// Network Layer
class NetworkLayer extends Layer {
    private String sourceIP;
    private String destIP;

    public NetworkLayer(Layer lower, Layer upper, String sourceIP, String destIP) {
        super(lower, upper);
        this.sourceIP = sourceIP;
        this.destIP = destIP != null ? destIP : "unknown";
    }

    @Override
    String send(String data) {
        String packet = "IP:" + sourceIP + "→" + destIP + "|" + data;
        System.out.println("Network: Adicionando IP → " + packet);
        return lowerLayer.send(packet);
    }

    @Override
    String receive(String data) {
        if (data == null || !data.contains("|")) return data;
        String[] parts = data.split("\\|", 2);
        System.out.println("Network: Removendo IP (" + parts[0] + ")");
        return parts.length > 1 ? parts[1] : data;
    }

    public void setDestIP(String destIP) {
        this.destIP = destIP;
    }
}

// Transport Layer
class TransportLayer extends Layer {
    private int sourcePort;
    private int destPort;

    public TransportLayer(Layer lower, Layer upper, int sourcePort, int destPort) {
        super(lower, upper);
        this.sourcePort = sourcePort;
        this.destPort = destPort;
    }

    @Override
    String send(String data) {
        String segment = "PORT:" + sourcePort + "→" + destPort + "|" + data;
        System.out.println("Transport: Adicionando porta → " + segment);
        return lowerLayer.send(segment);
    }

    @Override
    String receive(String data) {
        if (data == null || !data.contains("|")) return data;
        String[] parts = data.split("\\|", 2);
        System.out.println("Transport: Removendo porta (" + parts[0] + ")");
        return parts.length > 1 ? parts[1] : data;
    }
}

// Session Layer
class SessionLayer extends Layer {
    private String sessionID;

    public SessionLayer(Layer lower, Layer upper, String sessionID) {
        super(lower, upper);
        this.sessionID = sessionID;
    }

    @Override
    String send(String data) {
        String sess = "SESS:" + sessionID + "|" + data;
        System.out.println("Session: Adicionando sessão → " + sess);
        return lowerLayer.send(sess);
    }

    @Override
    String receive(String data) {
        if (data == null || !data.contains("|")) return data;
        String[] parts = data.split("\\|", 2);
        System.out.println("Session: Removendo sessão (" + parts[0] + ")");
        return parts.length > 1 ? parts[1] : data;
    }
}

// Presentation Layer (com criptografia opcional)
class PresentationLayer extends Layer {
    private boolean encrypt = false;
    private static final String KEY = "chave12345678901"; // 16 bytes para AES

    public PresentationLayer(Layer lower, Layer upper) {
        super(lower, upper);
    }

    public void enableEncryption() {
        this.encrypt = true;
    }

    @Override
    String send(String data) {
        if (encrypt) {
            try {
                SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.ENCRYPT_MODE, keySpec);
                byte[] enc = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
                String encrypted = Base64.getEncoder().encodeToString(enc);
                System.out.println("Presentation: Criptografado → " + encrypted);
                return lowerLayer.send(encrypted);
            } catch (Exception e) {
                System.out.println("Erro cripto: " + e.getMessage());
                return lowerLayer.send(data);
            }
        }
        System.out.println("Presentation: Sem cripto → " + data);
        return lowerLayer.send(data);
    }

    @Override
    String receive(String data) {
        if (encrypt) {
            try {
                SecretKeySpec keySpec = new SecretKeySpec(KEY.getBytes(StandardCharsets.UTF_8), "AES");
                Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
                cipher.init(Cipher.DECRYPT_MODE, keySpec);
                byte[] dec = cipher.doFinal(Base64.getDecoder().decode(data));
                String decrypted = new String(dec, StandardCharsets.UTF_8);
                System.out.println("Presentation: Descriptografado → " + decrypted);
                return decrypted;
            } catch (Exception e) {
                System.out.println("Erro descripto: " + e.getMessage());
                return data;
            }
        }
        System.out.println("Presentation: Sem cripto → " + data);
        return data;
    }
}

// Application Layer
class ApplicationLayer extends Layer {
    public ApplicationLayer(Layer lower) {
        super(lower, null);
    }

    @Override
    String send(String data) {
        System.out.println("Application: Enviando → " + data);
        return lowerLayer.send(data);
    }

    @Override
    String receive(String data) {
        System.out.println("Application: Recebida → " + data);
        return data;
    }
}

// Dispositivo
class Device {
    private String name;
    private String ip;
    private int port;
    private ApplicationLayer appLayer;
    private Map<String, Device> network = new HashMap<>();

    public Device(String name, String ip, int port) {
        this.name = name;
        this.ip = ip;
        this.port = port;
        buildStack();
    }

    private void buildStack() {
        PhysicalLayer phys = new PhysicalLayer(null);
        DataLinkLayer link = new DataLinkLayer(phys, null);
        NetworkLayer net = new NetworkLayer(link, null, ip, null);
        TransportLayer trans = new TransportLayer(net, null, port, 8080);
        SessionLayer sess = new SessionLayer(trans, null, "SESS-" + name);
        PresentationLayer pres = new PresentationLayer(sess, null);
        appLayer = new ApplicationLayer(pres);

        phys.upperLayer = link;
        link.upperLayer = net;
        net.upperLayer = trans;
        trans.upperLayer = sess;
        sess.upperLayer = pres;
        pres.upperLayer = appLayer;
    }

    public void enableEncryption() {
        Layer l = appLayer.lowerLayer;
        while (l != null && !(l instanceof PresentationLayer)) {
            l = l.lowerLayer;
        }
        if (l instanceof PresentationLayer) {
            ((PresentationLayer) l).enableEncryption();
            System.out.println("[" + name + "] Criptografia ativada");
        }
    }

    public void addDevice(Device d) {
        network.put(d.name, d);
    }

    public void sendMessage(String targetName, String message) {
        Device target = network.get(targetName);
        if (target == null) {
            System.out.println("[" + name + "] " + targetName + " não encontrado");
            return;
        }

        // Define IP destino
        Layer l = appLayer.lowerLayer;
        while (l != null && !(l instanceof NetworkLayer)) {
            l = l.lowerLayer;
        }
        if (l instanceof NetworkLayer) {
            ((NetworkLayer) l).setDestIP(target.ip);
        }

        System.out.println("\n[" + name + " → " + targetName + "] " + message);
        String transmitted = appLayer.send(message);

        System.out.println("\n--- transmissão simulada ---\n");

        target.receiveMessage(transmitted);
    }

    private void receiveMessage(String data) {
        Layer phys = appLayer.lowerLayer;
        while (phys != null && !(phys instanceof PhysicalLayer)) {
            phys = phys.lowerLayer;
        }
        if (phys != null) {
            phys.receive(data);
        }
    }

    public String getName() {
        return name;
    }
}

// Main
public class NetworkSimulation {
    public static void main(String[] args) {
        Device pc1 = new Device("PC1", "192.168.1.10", 50001);
        Device pc2 = new Device("PC2", "192.168.1.11", 50002);
        Device celular = new Device("Celular", "192.168.1.20", 50020);
        Device tv = new Device("SmartTV", "192.168.1.30", 50030);
        Device geladeira = new Device("Geladeira", "192.168.1.40", 50040);
        Device camera = new Device("Camera", "192.168.1.50", 50050);

        Device[] devices = {pc1, pc2, celular, tv, geladeira, camera};

        for (Device a : devices) {
            for (Device b : devices) {
                if (a != b) a.addDevice(b);
            }
        }

        Scanner sc = new Scanner(System.in);
        System.out.println("Simulador de rede doméstica");
        System.out.println("Comandos:");
        System.out.println("  send PC1 SmartTV Olá da sala!");
        System.out.println("  encrypt Geladeira");
        System.out.println("  exit\n");

        while (true) {
            System.out.print("> ");
            String line = sc.nextLine().trim();
            if (line.isEmpty()) continue;

            String[] parts = line.split("\\s+", 3);

            if (parts[0].equalsIgnoreCase("exit")) {
                break;
            }

            if (parts[0].equalsIgnoreCase("send") && parts.length >= 3) {
                String from = parts[1];
                String to = parts[2];
                String msg = parts.length > 3 ? parts[3] : "";

                Device source = find(devices, from);
                if (source != null) {
                    source.sendMessage(to, msg);
                } else {
                    System.out.println("Dispositivo não encontrado: " + from);
                }
            } else if (parts[0].equalsIgnoreCase("encrypt") && parts.length == 2) {
                Device d = find(devices, parts[1]);
                if (d != null) {
                    d.enableEncryption();
                } else {
                    System.out.println("Dispositivo não encontrado: " + parts[1]);
                }
            } else {
                System.out.println("Comando inválido. Tente send ou encrypt.");
            }
        }

        sc.close();
        System.out.println("Tchau!");
    }

    private static Device find(Device[] ds, String name) {
        for (Device d : ds) {
            if (d.getName().equalsIgnoreCase(name)) return d;
        }
        return null;
    }
}