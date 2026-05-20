package com.delivery.util;

import java.sql.*;

public class DatabaseConnection {
    private static final String DB_PATH = "delivery.db";
    private static final String URL = "jdbc:sqlite:" + DB_PATH;

    private static DatabaseConnection instance;
    private Connection connection;

    private DatabaseConnection() throws SQLException {
        try {
            Class.forName("org.sqlite.JDBC");
            this.connection = DriverManager.getConnection(URL);
            this.connection.createStatement().execute("PRAGMA foreign_keys = ON");
            initSchema();
        } catch (ClassNotFoundException e) {
            throw new SQLException("SQLite Driver not found: " + e.getMessage());
        }
    }

    public static DatabaseConnection getInstance() throws SQLException {
        if (instance == null || instance.connection.isClosed()) {
            instance = new DatabaseConnection();
        }
        return instance;
    }

    public Connection getConnection() throws SQLException {
        if (connection == null || connection.isClosed()) {
            connection = DriverManager.getConnection(URL);
            connection.createStatement().execute("PRAGMA foreign_keys = ON");
        }
        return connection;
    }

    private void initSchema() throws SQLException {
        Statement st = connection.createStatement();

        st.executeUpdate("CREATE TABLE IF NOT EXISTS clienti (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "nume TEXT NOT NULL," +
            "email TEXT NOT NULL UNIQUE," +
            "telefon TEXT NOT NULL," +
            "adresa TEXT," +
            "data_inregistrare TEXT NOT NULL)");

        st.executeUpdate("CREATE TABLE IF NOT EXISTS curieri (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "nume TEXT NOT NULL," +
            "telefon TEXT NOT NULL," +
            "vehicul TEXT NOT NULL," +
            "disponibil INTEGER NOT NULL DEFAULT 1)");

        st.executeUpdate("CREATE TABLE IF NOT EXISTS comenzi (" +
            "id INTEGER PRIMARY KEY AUTOINCREMENT," +
            "client_id INTEGER NOT NULL," +
            "curier_id INTEGER NOT NULL," +
            "adresa_livrare TEXT NOT NULL," +
            "status TEXT NOT NULL DEFAULT 'NOU'," +
            "total REAL NOT NULL DEFAULT 0," +
            "data_comanda TEXT NOT NULL," +
            "observatii TEXT," +
            "FOREIGN KEY (client_id) REFERENCES clienti(id)," +
            "FOREIGN KEY (curier_id) REFERENCES curieri(id)," +
            "CHECK (status IN ('NOU','IN_PREGATIRE','IN_LIVRARE','LIVRAT','ANULAT')))");

        ResultSet rs = st.executeQuery("SELECT COUNT(*) FROM clienti");
        boolean empty = rs.next() && rs.getInt(1) == 0;
        rs.close();
        st.close();

        if (empty) insertSampleData();
    }

    private void insertSampleData() throws SQLException {
        connection.setAutoCommit(false);
        try {
            Statement st = connection.createStatement();
            String[] clienti = {
                "('Alexandru Gațcan','alex.gatcan@gmail.com','069231045','str. Columna 170, ap.34','2023-01-12')",
                "('Maria Botnaru','maria.botnaru93@mail.ru','079452310','bd. Ștefan cel Mare 124, ap.7','2023-01-18')",
                "('Ion Negură','ion.negura@yahoo.com','068123987','str. Ismail 86, ap.12','2023-02-03')",
                "('Elena Moraru','elena.moraru@gmail.com','060341208','str. Albișoara 42, ap.3','2023-02-09')",
                "('Dumitru Postolachi','dumitru.postolachi@mail.md','061209876','str. Petricani 72','2023-02-14')",
                "('Natalia Prisăcaru','natalia.prisacaru@gmail.com','078903412','bd. Moscova 14, ap.88','2023-02-22')",
                "('Vasile Rusnac','vasile.rusnac@gmail.com','062781234','str. Mihai Eminescu 55','2023-03-01')",
                "('Cristina Leașcu','cristina.leascu@yahoo.com','063412098','str. Calea Ieșilor 33, ap.5','2023-03-07')",
                "('Andrei Cojocaru','andrei.cojocaru@gmail.com','064098712','str. Trandafirilor 9','2023-03-11')",
                "('Oxana Stratan','oxana.stratan@mail.ru','065234901','bd. Dacia 48, ap.21','2023-03-19')",
                "('Sergiu Grosu','sergiu.grosu.md@gmail.com','066780123','str. Vasile Alecsandri 17','2023-03-25')",
                "('Tatiana Vrabie','tatiana.vrabie@gmail.com','069012345','str. Independenței 90','2023-04-02')",
                "('Gheorghe Ciobanu','gheorghe.ciobanu@mail.md','068901234','str. Calea Orheiului 11, ap.4','2023-04-08')",
                "('Irina Rotaru','irina.rotaru1990@gmail.com','067012398','str. Florilor 28','2023-04-14')",
                "('Mihai Lupan','mihai.lupan@yahoo.com','078123049','str. Alba Iulia 75, ap.16','2023-04-20')",
                "('Lilia Grama','lilia.grama@gmail.com','079234105','str. Bogdan Voievod 3','2023-05-01')",
                "('Radu Platon','radu.platon.rp@gmail.com','060345216','bd. Traian 62, ap.9','2023-05-06')",
                "('Aliona Sturza','aliona.sturza@mail.ru','061456327','str. Gradinilor 51','2023-05-11')",
                "('Victor Oprea','victor.oprea@gmail.com','062567438','str. Renașterii 19, ap.2','2023-05-17')",
                "('Olga Balan','olga.balan.md@gmail.com','063678549','str. Tighina 44','2023-05-23')",
                "('Nicolae Cucu','nicolae.cucu@yahoo.com','064789650','str. Drumul Viilor 8','2023-06-01')",
                "('Diana Carp','diana.carp@gmail.com','065890761','str. Calea Moșilor 37, ap.11','2023-06-07')",
                "('Pavel Frunză','pavel.frunza@mail.md','066901872','str. Vasile Lupu 22','2023-06-13')",
                "('Svetlana Micu','svetlana.micu@gmail.com','069012983','str. Mitropolit Varlaam 6, ap.8','2023-06-19')",
                "('Petru Cebanu','petru.cebanu@yahoo.com','068123094','str. Decebal 31','2023-06-25')",
                "('Valentina Guțu','valentina.gutu@gmail.com','067234105','str. Calea Basarabiei 14','2023-07-01')",
                "('Tudor Negara','tudor.negara@mail.ru','078345216','str. Columna 88, ap.25','2023-07-07')",
                "('Marina Chiriac','marina.chiriac@gmail.com','079456327','str. Ismail 113','2023-07-13')",
                "('Aurel Macari','aurel.macari@yahoo.com','060567438','bd. Ștefan cel Mare 200, ap.5','2023-07-19')",
                "('Rodica Tincu','rodica.tincu@gmail.com','061678549','str. Petricani 45','2023-07-25')",
                "('Octavian Harea','octavian.harea@mail.md','062789650','str. Albișoara 67, ap.14','2023-08-01')",
                "('Luminița Smerea','luminita.smerea@gmail.com','063890761','bd. Moscova 89','2023-08-07')",
                "('Dorin Vicol','dorin.vicol.dv@gmail.com','064901872','str. Mihai Eminescu 32, ap.6','2023-08-13')",
                "('Anca Buciuceanu','anca.buciuceanu@yahoo.com','065012983','str. Calea Ieșilor 77','2023-08-19')",
                "('Ștefan Nour','stefan.nour@gmail.com','066123094','str. Trandafirilor 54, ap.3','2023-08-25')",
                "('Adriana Antoci','adriana.antoci@mail.ru','069234105','bd. Dacia 12','2023-09-01')",
                "('Vlad Morosan','vlad.morosan@gmail.com','068345216','str. Vasile Alecsandri 66','2023-09-07')",
                "('Corina Toma','corina.toma.ct@gmail.com','067456327','str. Independenței 23, ap.17','2023-09-13')",
                "('Bogdan Voinea','bogdan.voinea@yahoo.com','078567438','str. Calea Orheiului 88','2023-09-19')",
                "('Viorica Lungu','viorica.lungu@gmail.com','079678549','str. Florilor 41, ap.2','2023-09-25')",
                "('Laurențiu Gaf','laurentiu.gaf@mail.md','060789650','bd. Traian 35','2023-10-01')",
                "('Simona Buga','simona.buga@gmail.com','061890761','str. Bogdan Voievod 19, ap.7','2023-10-07')",
                "('Eugen Sandu','eugen.sandu.es@gmail.com','062901872','str. Gradinilor 66','2023-10-13')",
                "('Camelia Marin','camelia.marin@yahoo.com','063012983','str. Renașterii 55','2023-10-19')",
                "('Florin Popa','florin.popa.fp@gmail.com','064123094','str. Tighina 18, ap.9','2023-10-25')",
                "('Mihaela Coban','mihaela.coban@mail.ru','065234105','str. Drumul Viilor 27','2023-11-01')",
                "('Ionuț Boico','ionut.boico@gmail.com','066345216','str. Calea Moșilor 62, ap.4','2023-11-07')",
                "('Lidia Mija','lidia.mija@yahoo.com','069456327','str. Vasile Lupu 38','2023-11-13')",
                "('Răzvan Pasat','razvan.pasat@gmail.com','068567438','str. Mitropolit Varlaam 44, ap.1','2023-11-19')",
                "('Sorina Leahu','sorina.leahu@mail.md','067678549','str. Decebal 77','2023-11-25')"
            };
            for (String v : clienti)
                st.executeUpdate("INSERT INTO clienti (nume,email,telefon,adresa,data_inregistrare) VALUES " + v);

            String[] curieri = {
                "('Maxim Railean','079701001','Bicicletă electrică Xiaomi',1)",
                "('Andrei Șișcanu','060702002','Scuter Honda PCX 125',1)",
                "('Ion Damaschin','061703003','Toyota Corolla 2019',1)",
                "('Vitalie Cobzaru','062704004','Scuter Yamaha NMAX',1)",
                "('Cornel Bivol','063705005','Bicicletă electrică Xiaomi',0)",
                "('Sergiu Bahnaru','064706006','Dacia Logan 2020',1)",
                "('Mihail Savițchi','065707007','Scuter Honda PCX 125',1)",
                "('Alexei Cașu','066708008','Volkswagen Polo 2018',0)",
                "('Gheorghe Răileanu','069709009','Bicicletă electrică Kugoo',1)",
                "('Vasile Zgardan','068710010','Scuter Suzuki Address',1)",
                "('Tudor Barcari','067711011','Ford Focus 2017',1)",
                "('Eugen Musteață','078712012','Scuter Yamaha NMAX',0)",
                "('Radu Crețu','079713013','Bicicletă electrică Kugoo',1)",
                "('Florin Borozan','060714014','Renault Logan 2021',1)",
                "('Cristian Popovici','061715015','Scuter Honda PCX 125',1)"
            };
            for (String v : curieri)
                st.executeUpdate("INSERT INTO curieri (nume,telefon,vehicul,disponibil) VALUES " + v);

            Object[][] comenzi = {
                {1,1,"str. Columna 170, ap.34","LIVRAT",187.00,"2023-12-01 10:15","Lăsați la ușă"},
                {2,2,"bd. Ștefan cel Mare 124, ap.7","LIVRAT",234.50,"2023-12-01 10:42",null},
                {3,3,"str. Ismail 86, ap.12","LIVRAT",312.00,"2023-12-01 11:08","Fără ceapă"},
                {4,4,"str. Albișoara 42, ap.3","LIVRAT",98.00,"2023-12-01 11:35",null},
                {5,5,"str. Petricani 72","ANULAT",156.00,"2023-12-01 12:00","Client nu răspunde"},
                {6,6,"bd. Moscova 14, ap.88","LIVRAT",267.50,"2023-12-01 12:20",null},
                {7,7,"str. Mihai Eminescu 55","LIVRAT",189.00,"2023-12-01 12:55","Etaj 4, interfon 55"},
                {8,8,"str. Calea Ieșilor 33, ap.5","LIVRAT",345.00,"2023-12-01 13:15",null},
                {9,9,"str. Trandafirilor 9","LIVRAT",143.00,"2023-12-01 13:40","Sunați înainte"},
                {10,10,"bd. Dacia 48, ap.21","LIVRAT",456.00,"2023-12-01 14:05",null},
                {11,11,"str. Vasile Alecsandri 17","LIVRAT",223.50,"2023-12-02 09:10",null},
                {12,12,"str. Independenței 90","ANULAT",167.00,"2023-12-02 09:38","Comandă duplicată"},
                {13,13,"str. Calea Orheiului 11, ap.4","LIVRAT",298.00,"2023-12-02 10:02",null},
                {14,14,"str. Florilor 28","LIVRAT",178.50,"2023-12-02 10:27",null},
                {15,15,"str. Alba Iulia 75, ap.16","LIVRAT",389.00,"2023-12-02 10:55","Fără sare"},
                {16,1,"str. Bogdan Voievod 3","LIVRAT",245.00,"2023-12-02 11:18",null},
                {17,2,"bd. Traian 62, ap.9","LIVRAT",134.00,"2023-12-02 11:43",null},
                {18,3,"str. Gradinilor 51","IN_LIVRARE",276.50,"2023-12-02 12:10",null},
                {19,4,"str. Renașterii 19, ap.2","LIVRAT",198.00,"2023-12-02 12:35",null},
                {20,5,"str. Tighina 44","LIVRAT",321.00,"2023-12-02 13:00",null},
                {21,6,"str. Drumul Viilor 8","LIVRAT",287.50,"2023-12-03 09:05","Urgent!"},
                {22,7,"str. Calea Moșilor 37, ap.11","LIVRAT",156.00,"2023-12-03 09:30",null},
                {23,8,"str. Vasile Lupu 22","ANULAT",234.00,"2023-12-03 09:58","Fără răspuns la ușă"},
                {24,9,"str. Mitropolit Varlaam 6, ap.8","LIVRAT",367.00,"2023-12-03 10:22",null},
                {25,10,"str. Decebal 31","LIVRAT",189.50,"2023-12-03 10:47",null},
                {26,11,"str. Calea Basarabiei 14","LIVRAT",412.00,"2023-12-03 11:15",null},
                {27,12,"str. Columna 88, ap.25","LIVRAT",223.00,"2023-12-03 11:40","Etaj 5"},
                {28,13,"str. Ismail 113","LIVRAT",178.00,"2023-12-03 12:05",null},
                {29,14,"bd. Ștefan cel Mare 200, ap.5","LIVRAT",298.50,"2023-12-03 12:30",null},
                {30,15,"str. Petricani 45","IN_LIVRARE",334.00,"2023-12-03 12:58",null},
                {31,1,"str. Albișoara 67, ap.14","LIVRAT",167.50,"2023-12-04 09:00",null},
                {32,2,"bd. Moscova 89","LIVRAT",245.00,"2023-12-04 09:25",null},
                {33,3,"str. Mihai Eminescu 32, ap.6","LIVRAT",378.00,"2023-12-04 09:52","Fără ardei iute"},
                {34,4,"str. Calea Ieșilor 77","ANULAT",123.00,"2023-12-04 10:18",null},
                {35,5,"str. Trandafirilor 54, ap.3","LIVRAT",289.50,"2023-12-04 10:43",null},
                {36,6,"bd. Dacia 12","LIVRAT",198.00,"2023-12-04 11:08",null},
                {37,7,"str. Vasile Alecsandri 66","LIVRAT",423.00,"2023-12-04 11:35",null},
                {38,8,"str. Independenței 23, ap.17","LIVRAT",234.50,"2023-12-04 12:00",null},
                {39,9,"str. Calea Orheiului 88","LIVRAT",167.00,"2023-12-04 12:25",null},
                {40,10,"str. Florilor 41, ap.2","LIVRAT",312.00,"2023-12-04 12:52",null},
                {41,11,"bd. Traian 35","LIVRAT",256.50,"2023-12-05 09:00",null},
                {42,12,"str. Bogdan Voievod 19, ap.7","ANULAT",189.00,"2023-12-05 09:28","Comandă dublă"},
                {43,13,"str. Gradinilor 66","LIVRAT",334.00,"2023-12-05 09:55",null},
                {44,14,"str. Renașterii 55","LIVRAT",212.50,"2023-12-05 10:20",null},
                {45,15,"str. Tighina 18, ap.9","LIVRAT",178.00,"2023-12-05 10:47",null},
                {46,1,"str. Drumul Viilor 27","LIVRAT",389.00,"2023-12-05 11:12",null},
                {47,2,"str. Calea Moșilor 62, ap.4","LIVRAT",223.50,"2023-12-05 11:38",null},
                {48,3,"str. Vasile Lupu 38","LIVRAT",267.00,"2023-12-05 12:05","Sunați la interfon"},
                {49,4,"str. Mitropolit Varlaam 44, ap.1","LIVRAT",156.50,"2023-12-05 12:30",null},
                {50,5,"str. Decebal 77","IN_LIVRARE",312.00,"2023-12-05 12:58",null},
                {1,6,"str. Columna 170, ap.34","LIVRAT",198.00,"2023-12-06 09:05",null},
                {2,7,"bd. Ștefan cel Mare 124, ap.7","LIVRAT",267.50,"2023-12-06 09:30",null},
                {3,8,"str. Ismail 86, ap.12","LIVRAT",345.00,"2023-12-06 09:58",null},
                {6,9,"bd. Moscova 14, ap.88","LIVRAT",189.00,"2023-12-06 10:22",null},
                {7,10,"str. Mihai Eminescu 55","ANULAT",223.50,"2023-12-06 10:47",null},
                {8,11,"str. Calea Ieșilor 33, ap.5","LIVRAT",312.00,"2023-12-06 11:15",null},
                {10,12,"bd. Dacia 48, ap.21","LIVRAT",167.00,"2023-12-06 11:40",null},
                {13,13,"str. Calea Orheiului 11, ap.4","LIVRAT",445.00,"2023-12-06 12:05","Etaj 2"},
                {15,14,"str. Alba Iulia 75, ap.16","LIVRAT",198.50,"2023-12-06 12:30",null},
                {18,15,"str. Gradinilor 51","LIVRAT",278.00,"2023-12-06 12:58",null},
                {20,1,"str. Tighina 44","LIVRAT",189.00,"2023-12-07 09:05",null},
                {22,2,"str. Calea Moșilor 37, ap.11","LIVRAT",334.50,"2023-12-07 09:30",null},
                {25,3,"str. Decebal 31","IN_LIVRARE",212.00,"2023-12-07 09:58",null},
                {27,4,"str. Columna 88, ap.25","LIVRAT",267.00,"2023-12-07 10:22",null},
                {30,5,"str. Petricani 45","LIVRAT",145.50,"2023-12-07 10:47",null},
                {32,6,"bd. Moscova 89","LIVRAT",389.00,"2023-12-07 11:15",null},
                {35,7,"str. Trandafirilor 54, ap.3","LIVRAT",234.00,"2023-12-07 11:40",null},
                {38,8,"str. Independenței 23, ap.17","LIVRAT",178.50,"2023-12-07 12:05",null},
                {40,9,"str. Florilor 41, ap.2","ANULAT",298.00,"2023-12-07 12:30","Adresă greșită"},
                {42,10,"str. Bogdan Voievod 19, ap.7","LIVRAT",212.00,"2023-12-07 12:58",null},
                {44,11,"str. Renașterii 55","LIVRAT",356.50,"2023-12-08 09:00",null},
                {46,12,"str. Drumul Viilor 27","LIVRAT",189.00,"2023-12-08 09:25",null},
                {48,13,"str. Vasile Lupu 38","LIVRAT",267.00,"2023-12-08 09:52",null},
                {50,14,"str. Decebal 77","LIVRAT",323.50,"2023-12-08 10:18",null},
                {1,15,"str. Columna 170, ap.34","LIVRAT",167.00,"2023-12-08 10:43",null},
                {5,1,"str. Petricani 72","LIVRAT",245.50,"2023-12-08 11:08",null},
                {10,2,"bd. Dacia 48, ap.21","LIVRAT",389.00,"2023-12-08 11:35",null},
                {15,3,"str. Alba Iulia 75, ap.16","IN_LIVRARE",212.50,"2023-12-08 12:00",null},
                {20,4,"str. Tighina 44","LIVRAT",278.00,"2023-12-08 12:25",null},
                {25,5,"str. Decebal 31","LIVRAT",156.00,"2023-12-08 12:52",null},
                {30,6,"str. Petricani 45","LIVRAT",334.50,"2023-12-09 09:00",null},
                {35,7,"str. Trandafirilor 54, ap.3","ANULAT",198.00,"2023-12-09 09:25",null},
                {40,8,"str. Florilor 41, ap.2","LIVRAT",278.50,"2023-12-09 09:52",null},
                {45,9,"str. Tighina 18, ap.9","LIVRAT",189.00,"2023-12-09 10:18",null},
                {50,10,"str. Decebal 77","LIVRAT",423.00,"2023-12-09 10:43","Urgent!"},
                {3,11,"str. Ismail 86, ap.12","LIVRAT",234.50,"2023-12-09 11:08",null},
                {8,12,"str. Calea Ieșilor 33, ap.5","LIVRAT",312.00,"2023-12-09 11:35",null},
                {13,13,"str. Calea Orheiului 11, ap.4","LIVRAT",178.00,"2023-12-09 12:00",null},
                {18,14,"str. Gradinilor 51","LIVRAT",256.50,"2023-12-09 12:25",null},
                {23,15,"str. Vasile Lupu 22","LIVRAT",345.00,"2023-12-09 12:52",null},
                {28,1,"str. Ismail 113","NOU",198.00,"2023-12-10 09:00",null},
                {33,2,"str. Mihai Eminescu 32, ap.6","NOU",278.50,"2023-12-10 09:25",null},
                {38,3,"str. Independenței 23, ap.17","IN_PREGATIRE",167.00,"2023-12-10 09:52",null},
                {43,4,"str. Gradinilor 66","IN_PREGATIRE",312.00,"2023-12-10 10:18",null},
                {48,5,"str. Vasile Lupu 38","NOU",245.00,"2023-12-10 10:43",null},
                {2,6,"bd. Ștefan cel Mare 124, ap.7","IN_LIVRARE",189.50,"2023-12-10 11:08",null},
                {7,7,"str. Mihai Eminescu 55","NOU",367.00,"2023-12-10 11:35","Fără usturoi"},
                {12,8,"str. Independenței 90","IN_PREGATIRE",223.00,"2023-12-10 12:00",null},
                {17,9,"bd. Traian 62, ap.9","NOU",298.50,"2023-12-10 12:25",null},
                {22,10,"str. Calea Moșilor 37, ap.11","IN_LIVRARE",412.00,"2023-12-10 12:52",null}
            };

            PreparedStatement ps = connection.prepareStatement(
                "INSERT INTO comenzi (client_id,curier_id,adresa_livrare,status,total,data_comanda,observatii) VALUES (?,?,?,?,?,?,?)");
            for (Object[] c : comenzi) {
                ps.setInt(1, (Integer) c[0]);
                ps.setInt(2, (Integer) c[1]);
                ps.setString(3, (String) c[2]);
                ps.setString(4, (String) c[3]);
                ps.setDouble(5, (Double) c[4]);
                ps.setString(6, (String) c[5]);
                ps.setString(7, (String) c[6]);
                ps.executeUpdate();
            }
            ps.close();
            st.close();
            connection.commit();
        } catch (SQLException e) {
            connection.rollback();
            throw e;
        } finally {
            connection.setAutoCommit(true);
        }
    }
}
