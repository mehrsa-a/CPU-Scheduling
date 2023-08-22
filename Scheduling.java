import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Scheduling {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String algorithmName;
        int processCount;
        int timeQuantum = 0;
        Process[] processes;
        String processName;
        int arrivalTime = 0;
        int burstTime;
        int periodTime = 0;

        System.out.println("enter the name of scheduling algorithm that you want to use:");
        algorithmName = scanner.nextLine();
        System.out.println("enter the processes number that you want to schedule:");
        processCount = scanner.nextInt();
        processes = new Process[processCount];
        if(algorithmName.equals("Round Robin")){
            System.out.println("enter the time quantum:");
            timeQuantum = scanner.nextInt();
        }

        if(algorithmName.equals("Round Robin") || algorithmName.equals("FIFO")){
            for (int i = 0; i < processCount; i++) {
                System.out.println("enter the process name:");
                scanner.nextLine();
                processName = scanner.nextLine();
                System.out.println("enter the process arrival time:");
                arrivalTime = scanner.nextInt();
                System.out.println("enter the process burst time:");
                burstTime = scanner.nextInt();
                processes[i] = new Process(processName, arrivalTime, burstTime, periodTime);
            }
        } else {
            for (int i = 0; i < processCount; i++) {
                System.out.println("enter the process name:");
                scanner.nextLine();
                processName = scanner.nextLine();
                System.out.println("enter the process burst time:");
                burstTime = scanner.nextInt();
                System.out.println("enter the process period time:");
                periodTime = scanner.nextInt();
                processes[i] = new Process(processName, arrivalTime, burstTime, periodTime);
            }
        }

        if(algorithmName.equals("Round Robin")){
            RR(processes, timeQuantum);
        } else if(algorithmName.equals("FIFO")){
            FIFO(processes);
        } else {
            EDF(processes);
        }

    }

    public static void RR(Process[] processes, int timeQuantum){
        int startTime = 0;

        int l = processes.length;

        for (int i = 0; i < l; i++) {
            for (int j = 1; j < (l-i); j++) {
                if(processes[j-1].arrivalTime > processes[j].arrivalTime){
                    Process temp = processes[j-1];
                    processes[j-1] = processes[j];
                    processes[j] = temp;
                }
            }
        }

        System.out.print(processes[0].name + " (");
        if(processes[0].arrivalTime > startTime){
            startTime = processes[0].arrivalTime;
        }

        System.out.print(startTime + " - ");
        if(processes[0].burstTime <= timeQuantum){
            startTime += processes[0].burstTime;
            processes[0].burstTime = 0;
        } else {
            startTime += timeQuantum;
            processes[0].burstTime -= timeQuantum;
        }

        System.out.println(startTime + ")");

        List<Process> readyQueue = new ArrayList<>();
        for (int i = 1; i < l; i++) {
            if(processes[i].arrivalTime <= startTime){
                readyQueue.add(processes[i]);
            }
        }
        if(processes[0].burstTime != 0){
            readyQueue.add(processes[0]);
        }

        Out: while (true){
            for (Process process : processes) {
                if(process.name.equals(readyQueue.get(0).name)){

                    System.out.print(process.name + " (");
                    System.out.print(startTime + " - ");

                    if (process.burstTime <= timeQuantum) {
                        startTime += process.burstTime;
                        process.burstTime = 0;
                        for (Process p : processes) {
                            if (p.name.equals(process.name)) {
                                p.burstTime = process.burstTime;
                            }
                        }
                    } else {
                        startTime += timeQuantum;
                        process.burstTime -= timeQuantum;
                        for (Process p : processes) {
                            if (p.name.equals(process.name)) {
                                p.burstTime = process.burstTime;
                            }
                        }
                    }

                    System.out.println(startTime + ")");

                    readyQueue.remove(process);
                    for (int i = 0; i < l; i++) {
                        if (processes[i].name.equals(process.name)) {
                            for (int j = i + 1; j < l; j++) {
                                if (processes[j].arrivalTime <= startTime && processes[j].burstTime != 0 && !readyQueue.contains(processes[j])) {
                                    readyQueue.add(processes[j]);
                                }
                            }
                        }
                    }

                    if (process.burstTime != 0) {
                        readyQueue.add(process);
                    }
                    if (readyQueue.isEmpty()) {
                        System.out.println("happens");
                        break Out;
                    }
                }
            }
        }
    }

    public static void FIFO(Process[] processes){
        int startTime = 0;

        int l = processes.length;

        for (int i = 0; i < l; i++) {
            for (int j = 1; j < (l-i); j++) {
                if(processes[j-1].arrivalTime > processes[j].arrivalTime){
                    Process temp = processes[j-1];
                    processes[j-1] = processes[j];
                    processes[j] = temp;
                }
            }
        }

        for (Process process : processes) {
            System.out.print(process.name + " (");
            if(process.arrivalTime > startTime){
                startTime = process.arrivalTime;
            }

            System.out.print(startTime + " - ");
            startTime += process.burstTime;
            System.out.println(startTime + ")");
        }
    }

    public static void EDF(Process[] processes){
        int startTime = 0;
        int counter = 0;

        int l = processes.length;

        for (int i = 0; i < l; i++) {
            for (int j = 1; j < (l-i); j++) {
                if(processes[j-1].periodTime > processes[j].periodTime){
                    Process temp = processes[j-1];
                    processes[j-1] = processes[j];
                    processes[j] = temp;
                }
            }
        }

        while (startTime <= 120){
            Out: for (int i = 0; i < l; i++) {
                if(startTime>=processes[counter].arrivalTime){
                    System.out.print(processes[counter].name + " (");
                    System.out.print(startTime + " - ");
                    for (int j = 0; j < counter; j++) {
                        if(startTime + processes[counter].burstTime > processes[j].arrivalTime && processes[j].periodTime<processes[counter].periodTime){
                            processes[counter].burstTime -= (processes[j].arrivalTime-startTime);
                            startTime = processes[j].arrivalTime;
                            System.out.println(startTime + ")");
                            System.out.print(processes[j].name + " (");
                            System.out.print(startTime + " - ");
                            startTime += processes[j].burstTime;
                            System.out.println(startTime + ")");
                            processes[j].periodTime += processes[j].fixedPeriodTime;
                            processes[j].arrivalTime += processes[j].fixedPeriodTime;
                            System.out.print(processes[counter].name + " (");
                            System.out.print(startTime + " - ");
                            startTime += processes[counter].burstTime;
                            System.out.println(startTime + ")");
                            processes[counter].periodTime += processes[counter].fixedPeriodTime;
                            processes[counter].arrivalTime += processes[counter].fixedPeriodTime;
                            processes[counter].burstTime = processes[counter].fixedBurstTime;
                            break Out;
                        }
                    }

                    startTime += processes[counter].burstTime;
                    System.out.println(startTime + ")");
                    processes[counter].periodTime += processes[counter].fixedPeriodTime;
                    processes[counter].arrivalTime += processes[counter].fixedPeriodTime;
                    break;
                } else {
                    counter++;
                }
            }
            counter=0;

            //System.out.println("ptime = "+processes[0].periodTime);
            for (int i = 0; i < l; i++) {
                for (int j = 1; j < (l-i); j++) {
                    if(processes[j-1].periodTime > processes[j].periodTime){
                        Process temp = processes[j-1];
                        processes[j-1] = processes[j];
                        processes[j] = temp;
                    }
                }
            }
        }

    }
}

class Process{
    String name;
    int arrivalTime;
    int burstTime;
    int periodTime;
    int fixedPeriodTime;
    int fixedBurstTime;

    public Process(String name, int arrivalTime, int burstTime, int periodTime) {
        this.name = name;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
        this.periodTime = periodTime;
        fixedPeriodTime = periodTime;
        fixedBurstTime = burstTime;
    }
}
