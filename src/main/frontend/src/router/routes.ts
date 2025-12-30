import type { EntityType } from "../types/entity";

export const ROOT = "/";
export const SEARCH = `/search`;
export const CHANGELOG = "/changelog";
export const NOTIFICATIONS = "/notifications";
export const STRINGTEMPLATES = "/stringtemplates";
export const MAINTENANCE = "/maintenance";
export const ABOUT = "/about";
export const ENTITYNOTFOUND = "/404";
export const NOREADPERMISSIONS = "/nopermissions";
export const COREINTERFACECONFIGURATION = "/coreInterfaceConfiguration";

export const getSearchRoute = (type?: EntityType) => type ? `${SEARCH}/${type}` : SEARCH;

const createEntityRoutes = (path: string) =>
{
   const baseRoutes = {
      base: `/${path}`,
      search: `/search/${path}`,
      create: `/${path}/create`,
      detail: `/${path}/:uuid`,
   };
   return {
      ...baseRoutes,
      getDetail: (uuid: string) => `${baseRoutes.base}/${uuid}`,
   };
};

export default {
   laboratories: createEntityRoutes("labs"),
};
