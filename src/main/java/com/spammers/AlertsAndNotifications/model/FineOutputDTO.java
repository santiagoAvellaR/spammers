package com.spammers.AlertsAndNotifications.model;

import com.spammers.AlertsAndNotifications.model.dto.PaginatedResponseDTO;
import com.spammers.AlertsAndNotifications.model.enums.FineStatus;
import com.spammers.AlertsAndNotifications.model.enums.FineType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.domain.Page;

import java.time.LocalDate;
import java.util.List;

@AllArgsConstructor
@Getter
@Setter
public class FineOutputDTO {
    private String fineId;
    private String description;
    private float amount;
    private FineStatus fineStatus;
    private FineType fineType;
    private LocalDate expiredDate;
    private String bookTitle;

    /**
     * Converts a FineModel object into a FineOutputDTO.
     *
     * @param fineModel The FineModel entity containing all the fine's information.
     * @return A FineOutputDTO object that encapsulates the relevant fine details.
     */
    private static FineOutputDTO fineModelToOutputDTO(FineModel fineModel) {
        return new FineOutputDTO(
                fineModel.getFineId(),
                fineModel.getDescription(),
                fineModel.getAmount(),
                fineModel.getFineStatus(),
                fineModel.getFineType(),
                fineModel.getExpiredDate(),
                fineModel.getLoan().getBookName()
        );
    }

    /**
     * Encapsulates the data from a page of FineModel objects into a PaginatedResponseDTO.
     *
     * @param page The page of FineModel objects to be converted.
     * @return A PaginatedResponseDTO containing the list of FineOutputDTOs and pagination details.
     */
    public static PaginatedResponseDTO<FineOutputDTO> encapsulateFineModelOnDTO(Page<FineModel> page) {
        List<FineOutputDTO> fineOutputDTOList = page.getContent().stream()
                .map(FineOutputDTO::fineModelToOutputDTO)
                .toList();

        return new PaginatedResponseDTO<>(
                fineOutputDTOList,
                page.getNumber(),
                page.getTotalPages(),
                page.getTotalElements()
        );
    }
}
