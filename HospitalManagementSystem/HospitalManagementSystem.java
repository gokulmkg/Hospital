package HospitalManagementSystem;

import java.sql.*;
import java.util.Scanner;

public class HospitalManagementSystem {
    private static final String url ="jdbc:mysql://localhost:3306/hospital";
    private static final String user="root";
    private static final String password="M@hir007";
    public static void main(String[] args) {
        try{
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }

        Scanner scanner=new Scanner(System.in);
        try {
            Connection connection= DriverManager.getConnection(url,user,password);
            Patient patient=new Patient(connection,scanner);
            Doctor doctor=new Doctor(connection);
            while (true){
                System.out.println("HOSPITAL MANAGEMENT SYSTEM ");
                System.out.println("1. Add Patients ");
                System.out.println("2. View Patients ");
                System.out.println("3. View Doctors ");
                System.out.println("4. Book Appointment");
                System.out.println("5. Exit");
                System.out.print("Enter Your Choice: ");
                int choice=scanner.nextInt();
                switch (choice){
                    case 1:
                        //Add patients
                        patient.addPatient();
                        System.out.println();
                        break;
                    case 2:
                        // view patients
                        patient.viewPatients();
                        System.out.println();
                        break;
                    case 3:
                        //view doctors
                        doctor.viewDoctors();
                        System.out.println();
                        break;
                    case 4:
                        //book appointments
                        bookAppointment(patient,doctor,connection,scanner);
                        System.out.println();
                        break;
                    case 5:
                        //exit
                        System.out.println();
                        return;

                    default:
                        System.out.println("Enter Valid Choice");
                        System.out.println();
                        break;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }
    public static void bookAppointment(Patient patient, Doctor doctor, Connection connection, Scanner scanner) {
        System.out.print("Enter Patient Id: ");
        int patients = scanner.nextInt();
        System.out.print("Enter Doctors Id: ");
        int doctors = scanner.nextInt();
        System.out.print("Enter appointment date (YYYY-MM-DD): ");
        String appointmentDate = scanner.next();
        if (patient.getPatientById(patients) && doctor.getDoctorById(doctors)) {
            if (checkDoctorAvailability(patients, doctors, appointmentDate, connection)) {
                String appointmentQuery = "insert into appointment(patient_id,doctors_id,appointment_date) values (?,?,?)";
                try {
                    PreparedStatement preparedStatement = connection.prepareStatement(appointmentQuery);
                    preparedStatement.setInt(1, patients);
                    preparedStatement.setInt(2, doctors);
                    preparedStatement.setString(3, appointmentDate);
                    int rowsAffected = preparedStatement.executeUpdate();
                    if (rowsAffected > 0) {
                        System.out.println("Appointment Booked!");
                    } else {
                        System.out.println("Failed To Book Appointment!!");
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            } else {
                System.out.println("Doctor not available on this date!!");
            }
        } else {
            System.out.println("Either Doctor or Patient doesn't exist!!!");
        }
    }
    public static boolean checkDoctorAvailability(int patients,int doctors,String appointmentdate,Connection connection){
        String query ="select count(*) from appointment where patient_id= ? and doctors_id = ? and appointment_date=?";
        try{
            PreparedStatement preparedStatement=connection.prepareStatement(query);
            preparedStatement.setInt(1,patients);
            preparedStatement.setInt(2,doctors);
            preparedStatement.setString(3,appointmentdate);
            ResultSet resultSet=preparedStatement.executeQuery();
            if(resultSet.next()){
                int count=resultSet.getInt(1);
                if(count==0){
                    return true;
                }else {
                    return false ;
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
        return false;
    }
}