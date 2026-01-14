import java.util.Scanner;

public class SubnetCalculator {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        System.out.print("Digite o IP base (ex: 192.168.0.0): ");
        String ipStr = scanner.nextLine();
        System.out.print("Digite a máscara CIDR (ex: 24): ");
        int cidr = Integer.parseInt(scanner.nextLine());

        // Converte IP para array de ints
        String[] ipParts = ipStr.split("\\.");
        int[] ip = new int[4];
        for (int i = 0; i < 4; i++) {
            ip[i] = Integer.parseInt(ipParts[i]);
        }

        // Calcula máscara em decimal
        long mask = (0xFFFFFFFFL << (32 - cidr)) & 0xFFFFFFFFL;
        int[] maskArr = new int[4];
        maskArr[0] = (int)((mask >> 24) & 0xFF);
        maskArr[1] = (int)((mask >> 16) & 0xFF);
        maskArr[2] = (int)((mask >> 8) & 0xFF);
        maskArr[3] = (int)(mask & 0xFF);

        // Calcula endereço de rede (IP AND mask)
        int[] network = new int[4];
        for (int i = 0; i < 4; i++) {
            network[i] = ip[i] & maskArr[i];
        }

        // Calcula broadcast (rede OR ~mask)
        int[] broadcast = new int[4];
        long invMask = ~mask & 0xFFFFFFFFL;
        for (int i = 0; i < 4; i++) {
            broadcast[i] = network[i] | (int)((invMask >> (24 - i*8)) & 0xFF);
        }

        // Hosts utilizáveis
        long totalAddresses = 1L << (32 - cidr);
        long usableHosts = totalAddresses - 2;

        System.out.println("Rede: " + arrToIp(network));
        System.out.println("Broadcast: " + arrToIp(broadcast));
        System.out.println("Hosts utilizáveis: " + usableHosts);

        // Opção para dividir
        System.out.print("Quer dividir em quantas sub-redes? (0 para não): ");
        int numSubnets = Integer.parseInt(scanner.nextLine());
        if (numSubnets > 0) {
            int newCidr = cidr + (int) Math.ceil(Math.log(numSubnets) / Math.log(2));
            long subnetSize = 1L << (32 - newCidr);
            System.out.println("Nova máscara: /" + newCidr);
            for (int i = 0; i < numSubnets; i++) {
                int[] subNet = network.clone();
                long offset = i * subnetSize;
                // Adiciona offset ao IP de rede
                subNet[3] += (int)(offset & 0xFF);
                subNet[2] += (int)((offset >> 8) & 0xFF);
                subNet[1] += (int)((offset >> 16) & 0xFF);
                subNet[0] += (int)((offset >> 24) & 0xFF);
                System.out.println("Sub-rede " + (i+1) + ": " + arrToIp(subNet) + "/" + newCidr);
            }
        }
    }

    private static String arrToIp(int[] arr) {
        return arr[0] + "." + arr[1] + "." + arr[2] + "." + arr[3];
    }
}