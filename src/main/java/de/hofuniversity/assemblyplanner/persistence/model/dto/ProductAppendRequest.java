package de.hofuniversity.assemblyplanner.persistence.model.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public record ProductAppendRequest(

        @Schema(description = "Optional if \"products\" is specified. " +
                "describes a single product to append to an order. If \"products\" is also specified, " +
                "\"product\" is appended to products and thus processed after all other products have been processed.")
        UUID product,
        @Schema(description = "Optional if \"product\" is specified. Describes an array of products to append to the list " +
                "of products associated to the order in question.")
        List<UUID> products
) {
   public List<UUID> getProducts() {
       var list = products;
       if(list == null)
           list = new ArrayList<>();

       if(product != null)
           list.add(product);

       return list;
   }
}
