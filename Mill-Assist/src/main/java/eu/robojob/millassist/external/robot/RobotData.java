package eu.robojob.millassist.external.robot;

import eu.robojob.millassist.external.robot.AbstractRobotActionSettings.ApproachType;


public final class RobotData {
    
    public enum RobotRegister {
        MACHINE_ID(15);
        
        private int id;
        
        private RobotRegister(int id) {
            this.id = id;
        }
        
        public int getId() {
            return this.id;
        }
        
        public static RobotRegister getById(int id) {
            for (RobotRegister register: values()) {
                if (register.getId() == id)
                    return register;
            }
            return null;
        }
        
        public static RobotRegister getByString(String name) {
            for (RobotRegister register: values()) {
                if (register.toString().equals(name))
                    return register;
            }
            return null;
        }
    }
    
    public enum RobotUserFrame {
        UF1_STACKER(1), 
        UF3_MCH_WA1(3), 
        UF4_MCH_WA2(4), 
        UF6_PALLET_BIN(6),
        UF11_REGRIPPER(11);
        
        private int ufNr;
        
        private RobotUserFrame(int userFrameNr) {
            this.ufNr = userFrameNr;
        }
        
        public int getUfNr() {
            return this.ufNr;
        }
        
        public static RobotUserFrame getByUserFrameNr(int ufNr) {
            for (RobotUserFrame type: values()) {
                if (type.getUfNr() == ufNr)
                    return type;
            }
            return null;
        }
    }
    
    public enum RobotIPPoint {
        IP_UF1_AB(RobotUserFrame.UF1_STACKER.getUfNr(),2,ApproachType.TOP),
        IP_UF3_AB(RobotUserFrame.UF3_MCH_WA1.getUfNr(),2,ApproachType.TOP),
        IP_UF4_AB(RobotUserFrame.UF4_MCH_WA2.getUfNr(),2,ApproachType.TOP),
        IP_UF6_AB(RobotUserFrame.UF6_PALLET_BIN.getUfNr(),2,ApproachType.TOP),
        IP_UF11_AB_T(RobotUserFrame.UF11_REGRIPPER.getUfNr(),2,ApproachType.TOP),
        IP_UF11_AB_B(RobotUserFrame.UF11_REGRIPPER.getUfNr(),2,ApproachType.BOTTOM),
        IP_UF11_AB_L(RobotUserFrame.UF11_REGRIPPER.getUfNr(),2,ApproachType.LEFT),
        IP_UF11_AB_F(RobotUserFrame.UF11_REGRIPPER.getUfNr(),2,ApproachType.FRONT);
        
        private int ufNr, tfNr;
        private ApproachType posType;
        
        private RobotIPPoint(int ufNr, int tfNr, ApproachType posType) {
            this.ufNr = ufNr;
            this.tfNr = tfNr;
            this.posType = posType;
        }
        
        public int getUfNr() {
            return this.ufNr;
        }
        
        public int getTfNr() {
            return this.tfNr;
        }
        
        public ApproachType getPosType() {
            return this.posType;
        }
        
        public static RobotIPPoint getIPPoint(int ufNr, int tfNr, ApproachType posType) {
            for(RobotIPPoint type : values()) {
                if(type.getUfNr() == ufNr && type.getTfNr() == tfNr && type.getPosType() == posType) 
                    return type;
            }
            return null;
         } 
    }
    
    public enum RobotRefPoint {
        RP_UF3_A(RobotUserFrame.UF3_MCH_WA1.getUfNr(),2,2),
        RP_UF3_B(RobotUserFrame.UF3_MCH_WA1.getUfNr(),3,3),
        RP_UF3_AIR_A(RobotUserFrame.UF3_MCH_WA1.getUfNr(),10,2),
        RP_UF3_AIR_B(RobotUserFrame.UF3_MCH_WA1.getUfNr(),10,3),
        RP_UF4_A(RobotUserFrame.UF4_MCH_WA2.getUfNr(),2,2),
        RP_UF4_B(RobotUserFrame.UF4_MCH_WA2.getUfNr(),3,3),
        RP_UF4_AIR_A(RobotUserFrame.UF4_MCH_WA2.getUfNr(),10,2),
        RP_UF4_AIR_B(RobotUserFrame.UF4_MCH_WA2.getUfNr(),10,3);
        
        private int ufNr, tfNr, originalTfNr;
        
        private RobotRefPoint(int ufNr, int tfNr, int originalTfNr) {
            this.ufNr = ufNr;
            this.tfNr = tfNr;
            this.originalTfNr = originalTfNr;
        }

        public int getUfNr() {
            return this.ufNr;
        }

        public int getTfNr() {
            return this.tfNr;
        }

        public int getOriginalTfNr() {
            return this.originalTfNr;
        }

        public static RobotRefPoint getRPPoint(int ufNr, int tfNr, int originalTfNr) {
            for(RobotRefPoint type : values()) {
                if(type.getUfNr() == ufNr && type.getTfNr() == tfNr && type.getOriginalTfNr() == originalTfNr) 
                    return type;
            }
            return null;
        } 
    }

    public enum RobotSpecialPoint {
        HOME(0), JAW_CH(1), JAW_CH_APPR(2);

        private int id;
        
        private RobotSpecialPoint(int id) {
            this.id = id;
        }
        
        public int getId() {
            return this.id;
        }
        
        public static RobotSpecialPoint getById(int id) {
            for(RobotSpecialPoint type : values()) {
                if(type.getId() == id) 
                    return type;
            }
            return null;
         } 
    }
    
    public enum RobotToolFrame {
        TF2_SUB_A(2), 
        TF3_SUB_B(3), 
        TF4_SUB_C(4), 
        TF10_AIRBLOW(10);
        
        private int tfNr;
        
        private RobotToolFrame(int toolFrameNr) {
            this.tfNr = toolFrameNr;
        }
        
        public int getTfNr() {
            return this.tfNr;
        }
        
        public static RobotToolFrame getByToolFrameNr(int tfNr) {
            for (RobotToolFrame type: values()) {
                if (type.getTfNr() == tfNr)
                    return type;
            }
            return null;
        }
    }
}
