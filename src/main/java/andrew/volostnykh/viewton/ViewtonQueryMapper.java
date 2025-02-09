package andrew.volostnykh.viewton;

import java.util.Map;

public class ViewtonQueryMapper {
    public static ViewtonQuery of(Map<String, String> requestParams, int defaultPageSize) {
        return ViewtonQuery.builder()
                .rawWhereClauses(ViewtonMappersContext.mapWhereClauses.apply(requestParams))
                .rawOrderByes(ViewtonMappersContext.mapOrderByes.apply(requestParams))
                .page(ViewtonMappersContext.mapPage.apply(requestParams))
                .pageSize(ViewtonMappersContext.mapPageSize.apply(requestParams, defaultPageSize))
                .attributes(ViewtonMappersContext.mapAttributes.apply(requestParams))
                .totalAttributes(ViewtonMappersContext.mapTotalAttributes.apply(requestParams))
                .distinct(ViewtonMappersContext.isDistinct.apply(requestParams))
                .total(ViewtonMappersContext.isTotal.apply(requestParams))
                .count(ViewtonMappersContext.isCount.apply(requestParams))
                .build();
    }
}
