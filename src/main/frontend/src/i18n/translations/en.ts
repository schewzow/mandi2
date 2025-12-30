import {EntityTypes} from "../../types/entity.tsx";

export default function()
{
    const language = {
        english: "English",
        german: "German",
    };

    const common = {
        /** Entities */
        contact: "Contact",
        contacts: "Contacts",
        deputies: "Deputies",
        group: "User Group",
        groups: "User Groups",
        laboratory: "Laboratory",
        laboratories: "Laboratories",
        location: "Location",
        locations: "Locations",
        machine: "Machine",
        machines: "Machines",
        testObject: "Test Object",
        testObjects: "Test Objects",
        notifications: "Notifications",
        project: "Project",
        projects: "Projects",
        role: "Role",
        roles: "Roles",
        testObjectType: "Test Object Type",
        testObjectTypes: "Test Object Types",
        user: "User",
        users: "Users",
        unit: "Unit",
        units: "Units",
        testGroup: "Test Group",
        testGroups: "Test Groups",
        testModel: "Testmodel",
        testModels: "Testmodels",
        testModelMaster: "Testmodel Master",
        testModelMasters: "Testmodel Masters",
        testOrder: "Test Order",
        testOrders: "Test Orders",
        testModelMasterParameter: "Testmodel Master Parameter",
        testModelMasterParameters: "Testmodel Master Parameters",
        testModelParameter: "Testmodel Parameter",
        testModelParameters: "Testmodel Parameters",
        trial: "Trial",
        trials: "Trials",
        history: "History",

        entity: "Entity",
        newEntity: "New Entity",

        active: "Active",
        createdBy: "Created By",
        creator: "Creator",
        lastModifiedBy: "Modified By",
        delete: "Delete",
        search: "Search",
        email: "E-Mail",
        name: "Name",
        number: "Number",
        description: "Description",
        locked: "Locked",
        density: "Density (g/cm³)",
        owner: "Owner",
        apply: "Apply",
        close: "Close",
        cancel: "Cancel",
        remove: "Remove",
        removeAll: "Remove All",
        key: "Key",
        readonly: "Readonly",
        add: "Add",
        create: "Create",
        edit: "Edit",
        copy: "Copy",
        duplicate: "Duplicate",
        remarks: "Remarks",
        documents: "Documents ({count})",
        export: "Export",
        replace: "Replace",
        save: "Save",
        withdraw: "Withdraw",

        recalculate: "Recalculate",
        showOnlyActive: "Show only active",
        enterValidNumber: "Please enter a valid number.",

        loading: "Loading",
        no_options: "No Options",
        limit_exceeded: "Too many entries, please specify a filter.",
        limit_still_exceeded: "Too many entries, please make the filter more concrete.",

        displayName: "Display Name",
        type: "Type",
        lastUsedDate: "Last Used Date",

        resultValue: "Result Value",
    };

    const base = {
        uuid: "UUID",
        active: "Active",
        createdDate: "Created",
        lastModifiedDate: "Modified Date",
    };

    const entities = {
        base: {...base},
        [EntityTypes.laboratories]: {
            name: common.name,
            shortName: "Short name",
            labSwitchOn: "Lab-Switch-On",
            labSwitchOff: "Lab-Switch-Off",
        },
        [EntityTypes.users]: {
            firstname: "Firstname",
            lastname: "Lastname",
            fullName: "Full Name",
            language: "Language UI",
            initials: "Initials",
            email: common.email,
            location: common.location,
        },
    }

    return {
        common,
        language,
        entities,
    }
}