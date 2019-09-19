package cn.firstep.theflow.common;

/**
 * @author Alvin4u
 */
public class Permission {
    public static final String PERMISSION_DEFINITION_ACCESS = "flow:definition:access";
    public static final String PERMISSION_DEFINITION_EDIT = "flow:definition:edit";


    public static final String PERMISSION_PROCESS_ACCESS = "flow:process:access";
    public static final String PERMISSION_PROCESS_EDIT = "flow:process:edit";


    public static final String PERMISSION_TASK_ACCESS = "flow:task:access";
    public static final String PERMISSION_TASK_EDIT = "flow:task:edit";

    public static final String[] DEFINITION_MANAGER = {PERMISSION_DEFINITION_ACCESS, PERMISSION_DEFINITION_EDIT};
    public static final String[] PROCESS_MANAGER = {PERMISSION_PROCESS_ACCESS, PERMISSION_PROCESS_EDIT};
    public static final String[] TASK_MANAGER = {PERMISSION_TASK_ACCESS, PERMISSION_TASK_EDIT};
    public static final String[] ALL_ACCESS = {PERMISSION_DEFINITION_ACCESS, PERMISSION_PROCESS_ACCESS, PERMISSION_TASK_ACCESS};
    public static final String[] ALL_MANAGER = {PERMISSION_DEFINITION_ACCESS, PERMISSION_PROCESS_ACCESS, PERMISSION_TASK_ACCESS, PERMISSION_DEFINITION_EDIT, PERMISSION_PROCESS_EDIT, PERMISSION_TASK_EDIT};
}
