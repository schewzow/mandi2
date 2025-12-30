import {EntityTypes} from "../../types/entity.tsx";

export default function()
{
    const language = {
        english: "Englisch",
        german: "Deutsch",
    };

    const common = {
        /** Entities */
        contact: "Kontakt",
        contacts: "Kontakte",
        deputies: "Vertreter",
        group: "Benutzergruppe",
        groups: "Benutzergruppen",
        laboratory: "Labor",
        laboratories: "Labore",
        location: "Standort",
        locations: "Standorte",
        machine: "Maschine",
        machines: "Maschinen",
        testObject: "Testobjekt",
        testObjects: "Testobjekte",
        notifications: "Benachrichtigungen",
        project: "Projekt",
        projects: "Projekte",
        role: "Rolle",
        roles: "Rollen",
        testObjectType: "Test Objekttyp",
        testObjectTypes: "Test Objekttypen",
        user: "Benutzer",
        users: "Benutzer",
        unit: "Einheit",
        units: "Einheiten",
        testGroup: "Testgruppe",
        testGroups: "Testgruppen",
        testModel: "Testmodel",
        testModels: "Testmodelle",
        testModelMaster: "Testmodel Master",
        testModelMasters: "Testmodel Masters",
        testOrder: "Test Order",
        testOrders: "Test Orders",
        testModelMasterParameter: "Testmodel Master Parameter",
        testModelMasterParameters: "Testmodel Master Parameter",
        testModelParameter: "Testmodel Parameter",
        testModelParameters: "Testmodel Parameter",
        trial: "Versuch",
        trials: "Versuche",
        history: "Historie",

        entity: "Entität",
        newEntity: "Neue Entität",

        active: "Aktiv",
        createdBy: "Erstellt von",
        creator: "Ersteller",
        lastModifiedBy: "Bearbeitet von",
        delete: "Löschen",
        search: "Suchen",
        email: "E-Mail",
        name: "Name",
        number: "Nummer",
        description: "Beschreibung",
        locked: "Gesperrt",
        density: "Dichte (g/cm³)",
        owner: "Besitzer",
        apply: "Anwenden",
        close: "Schließen",
        cancel: "Abbrechen",
        remove: "Entfernen",
        removeAll: "Alle Entfernen",
        key: "Schlüssel",
        readonly: "Schreibgeschützt",
        add: "Hinzufügen",
        create: "Create",
        edit: "Edit",
        copy: "Kopieren",
        duplicate: "Duplizieren",
        remarks: "Bemerkungen",
        documents: "Dokumente ({count})",
        export: "Exportieren",
        replace: "Ersetzen",
        save: "Speichern",
        withdraw: "Widerrufen",

        recalculate: "Neu berechnen",
        showOnlyActive: "Nur aktive anzeigen",
        enterValidNumber: "Bitte eine gültige Nummer eingeben.",

        loading: "Laden",
        no_options: "Keine Optionen",
        limit_exceeded: "Zu viele Einträge, bitte Filterbedingung benutzen.",
        limit_still_exceeded: "Zu viele Einträge, bitte Filterbedingung erweitern.",

        displayName: "Anzeigename",
        type: "Typ",
        lastUsedDate: "Letzter Anwendungstag",

        resultValue: "Ergebniswert",
    }

    const base = {
        uuid: "UUID",
        active: "Aktiv",
        createdDate: "Erstellt",
        lastModifiedDate: "Bearbeitungsdatum",
    };

    const entities = {
        base: {...base},
        [EntityTypes.laboratories]: {
            name: common.name,
            shortName: "Kurzname",
            labSwitchOn: "Lab-Switch-Ein",
            labSwitchOff: "Lab-Switch-Aus",
        },
        [EntityTypes.users]: {
            firstname: "Vorname",
            lastname: "Nachname",
            fullName: "Nach-/Vorname",
            language: "Oberflächensprache",
            initials: "Initialien",
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