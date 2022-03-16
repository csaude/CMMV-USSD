package mz.org.fgh.vmmc.utils;

public class ConstantUtils {

       // menus
       public static final String MENU_REGISTER_NAME = "M004";
       public static final String MENU_REGISTER_SURNAME = "M006";
       public static final String MENU_REGISTER_AGE = "M008";
       public static final String MENU_PROVINCES_CODE = "M019";
       public static final String MENU_DISTRICTS_CODE = "M021";
       public static final String MENU_CELLNUMBER_FROM_SESSION_CODE = "M014";
       public static final String MENU_AUTHENTICATION_CODE = "M034";
       public static final String MENU_CLINICS_LIST_APPOINTMENT_CODE = "M043";
       public static final String MENU_APPOINTMENT_CONFIRMATION_CODE = "M050";
       public static final String MENU_CLINICS_LIST_CODE = "M056";
       public static final String MENU_APPOINTMENT_DETAILS_CODE = "M054";
       public static final String MENU_REGISTER_CONFIRMATION_CODE = "M030";
       public static final String MENU_SEND_SMS_CLINICS_LIST = "M041";
       public static final String MENU_APPOINTMENT_MONTH = "M048";
       public static final String MENU_APPOINTMENT_DAY = "M046";
       public static final String MENU_CONFIRMATION_SMS_CLINICS_LIST_CODE = "M059";
       public static final String MENU_INFORMATIVE_MESSAGES = "M062";
       
       // mensagens menus
       public static final String MENU_PRINCIPAL_DESCRIPTION = "CON Bem Vindo ao CMMV \n  1. Entrar \n  2. Registar-se";
       public static final String MENU_SESSION_RECOVER_DESCRIPTION = "CON Voltar para o passo anterior? \n  1. Sim \n  2. Nao";
       public static final String MENU_REGISTER_SUCCESS = "END O seu cadastro foi feito com sucesso \n Sera enviado um SMS com o codigo de acesso a sua conta.";
       public static final String MESSAGE_SEND_SMS_CLINIC_LIST = "END Sera enviado SMS com a lista de unidades sanidarias do seu distrito";
       public static final String MESSAGE_APPOINTMENT_NOT_CONFIRMED = "END A sua consulta nao foi marcada,Obrigado pela preferencia.";
       public static final String MESSAGE_APPOINTMENT_SUCCESS = "END A sua consulta foi marcada com sucesso.";
       public static final String MESSAGE_REGISTER_NOT_CONFIRMED = "END O seu registo nao foi efectuado,Obrigado pela preferencia.";

       // generic error message
       public static final String MESSAGE_UNEXPECTED_ERROR = "END Ocorreu um erro Inesperado.";
       public static final String MESSAGE_OPCAO_INVALIDA = "CON Opcao introduzida invalida. {0} \n";
       public static final String MESSAGE_APPOINTMENT_NOT_FOUND = "END Nao possui consulta marcada.";
       public static final String MESSAGE_OPCAO_INVALIDA_TERMINAR = "END Opcao introduzida invalida";
       public static final String MESSAGE_REGISTER_ERROR = "END Ocorreu erro ao efectuar o registo.";
       public static final String MESSAGE_LOGIN_FAILED = "CON Codigo de utilizador invalido. \n {0} ";
       public static final String MESSAGE_APPOINTMENT_FAILED = "END Ocorreu erro ao marcar consulta.";
       public static final String MESSAGE_APPOINTMENT_DETAILS = "\n Data da Consulta:  {0}  \n Unidade Sanitaria: {1} \n Estado da Consulta: {2}";
       public static final String MESSAGE_APPOINTMENT_DAY_INVALID = "CON O dia introduzido e invalido \n {0}.";
       public static final String MESSAGE_REGISTER_AGE_INVALID = "CON Idade invalida, A sua idade deve ser igual ou superior a 14 anos. \n {0}";
       public static final String MESSAGE_REGISTER_VALUE_INVALID = "CON O valor introduzido e invalido. \n {0}";
       public static final String MESSAGE_REGISTER_PASSWORD_SMS = "Bem vindo ao CMMV, O seu codigo de utilizador:{0}. Digite *123# e introduza o seu codigo.";
       public static final String MESSAGE_INFORMATIVE_MESSAGES  = "END Sera enviado SMS para: {0}  com informacao educativa.";

       // Selected Options
       public static final String TYPE_RECIEPIENT_MOBILE = "mobile";
       
       

}
