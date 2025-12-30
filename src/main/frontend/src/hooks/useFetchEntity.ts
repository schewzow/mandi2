import { useReducer, useEffect } from "react";
import type {RequestError} from "../api/api-error.ts";
import type {RequestResponse} from "../api/response.ts";
import type {FetchParams} from "../api/entity-api.ts";
import type {EntityType} from "../types/entity.tsx";
import type {Parameters} from "../api/api-utils.ts";

export const StateTypeEnum = {
   COMPLETED: "completed",
   ERROR: "error",
   FETCHING: "fetching",
   UPDATE_ENTITY: "update-entity",
} as const;

export type StateTypeE = (typeof StateTypeEnum)[keyof typeof StateTypeEnum];

type ReducerAction<Entity> = {
   type: typeof StateTypeEnum.COMPLETED,
   payload: Entity | null;
} | {
   type: typeof StateTypeEnum.ERROR,
   payload: RequestError;
} | {
   type: typeof StateTypeEnum.FETCHING,
} | {
   type: typeof StateTypeEnum.UPDATE_ENTITY,
   payload: Entity;
};

function reducer<Entity>(prevState: ReducerState<Entity>, action: ReducerAction<Entity>): ReducerState<Entity>
{
   switch (action.type)
   {
      case StateTypeEnum.FETCHING:
         return {
            loadingState: StateTypeEnum.FETCHING,
            entity: null,
         };
      case StateTypeEnum.COMPLETED:
         return {
            loadingState: StateTypeEnum.COMPLETED,
            entity: action.payload,
         };
      case StateTypeEnum.ERROR:
         return {
            loadingState: StateTypeEnum.ERROR,
            entity: null,
         };
      case StateTypeEnum.UPDATE_ENTITY:
         return {
            ...prevState,
            entity: action.payload,
         };
   }
   return prevState;
}

export type LoadingStateEnum =
    typeof StateTypeEnum.FETCHING |
    typeof StateTypeEnum.COMPLETED |
    typeof StateTypeEnum.ERROR;

interface ReducerState<Entity>
{
   entity: Entity | null;
   loadingState: LoadingStateEnum;
}

export interface UseFetchEntityResponse<Entity>
{
   entity: Entity | null;
   loadingState: LoadingStateEnum;
   setEntity: (entity: Entity) => void;
}

function useFetchEntity<Entity>(
   uuid: string,
   fetchEntity: (params: FetchParams) => Promise<RequestResponse<Entity>>,
   /**
    * The entity to navigate to when the GET got a 404.
    */
   entityType?: EntityType,
   params: Parameters = {},
): UseFetchEntityResponse<Entity>
{
   // const [
   //    {
   //       loadingState,
   //       entity,
   //    },
   //    dispatch,
   // ] = useReducer(reducer, {
   //    loadingState: uuid ? StateTypeEnum.FETCHING : StateTypeEnum.COMPLETED,
   //    entity: null,
   // });
   const [state, dispatch] = useReducer(
       reducer as React.Reducer<
           ReducerState<Entity>,
           ReducerAction<Entity>
       >,
       {
          loadingState: uuid
              ? StateTypeEnum.FETCHING
              : StateTypeEnum.COMPLETED,
          entity: null,
       }
   );
   // const { addError } = useErrorContext();
   // const history = useHistory();
   const { entity, loadingState } = state;

   useEffect(() =>
   {
      let didCancel = false;

      if (uuid)
      {
         // dispatch isFetching action so the current entity is removed
         if (loadingState !== StateTypeEnum.FETCHING) dispatch({ type: StateTypeEnum.FETCHING });

         fetchEntity({ uuid, urlParams: params }).then(response =>
         {
            if (!didCancel)
            {
               if (response.status === "success" && response.data)
               {
                  dispatch({ type: StateTypeEnum.COMPLETED, payload: response.data });
               }
               if (response.status === "error")
               {
                  // If we didn't find the entity go to our 404 page
                  if ((response.statusCode === 404 ||  response?.error?.global[0].key === "error.ResourceNotFoundException" ) && entityType != null)
                  {
                     if (uuid)
                     {
                        // const query = uuid.replace('key', '')
                        //   // .replace(/[\W_]+/g,''); //replace all non alphanumeric
                        //    .replace("=",'')
                        //    .replace("?",''); //replace all non alphanumeric

                        //const searchPath = `${getSearchRoute(entityType)}?query=${query}`
                        //history.replace(searchPath);
                     }
                     else
                     {
                        //history.replace(`${ENTITYNOTFOUND}/${entityType}`);
                     }


                  }
                  else if (response.error)
                  {
                     dispatch({ type: StateTypeEnum.ERROR, payload: response.error });
                     //addGlobalRequestErrors(addError, response.error);
                  }
               }
            }
         });
      }
      else
      {
         dispatch({ type: StateTypeEnum.COMPLETED, payload: null });
      }
      return () =>
      {
         didCancel = true;
      };
   },
   // only run on componentDidMount or when uuid changes
   [uuid]);

   return {
      entity: entity as Entity | null,
      loadingState,
      setEntity: (data: Entity) => dispatch({ type: StateTypeEnum.UPDATE_ENTITY, payload: data }),
   };
}

export default useFetchEntity;
