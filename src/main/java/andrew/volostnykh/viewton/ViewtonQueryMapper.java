package andrew.volostnykh.viewton;

import java.util.Map;

public class ViewtonQueryMapper {
    public static ViewtonQuery of(Map<String, String> requestParams, int defaultPageSize) {
        return ViewtonQuery.builder()
                .rawWhereClauses(ViewtonExtensionContext.mapWhereClauses.apply(requestParams))
                .rawOrderByes(ViewtonExtensionContext.mapOrderByes.apply(requestParams))
                .page(ViewtonExtensionContext.mapPage.apply(requestParams))
                .pageSize(ViewtonExtensionContext.mapPageSize.apply(requestParams, defaultPageSize))
                .attributes(ViewtonExtensionContext.mapAttributes.apply(requestParams))
                .totalAttributes(ViewtonExtensionContext.mapTotalAttributes.apply(requestParams))
                .distinct(ViewtonExtensionContext.isDistinct.apply(requestParams))
                .total(ViewtonExtensionContext.isTotal.apply(requestParams))
                .count(ViewtonExtensionContext.isCount.apply(requestParams))
                .build();
    }
}
