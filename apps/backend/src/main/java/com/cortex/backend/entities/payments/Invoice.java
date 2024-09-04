package com.cortex.backend.entities.payments;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDate;

@Entity
@Table(name = "invoices",
    indexes = @Index(name = "invoices_index_0", columnList = "transaction_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Invoice {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @OneToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "transaction_id", nullable = false)
  private Transaction transaction;

  @Column(name = "invoice_number", nullable = false)
  private String invoiceNumber;

  @Column(name = "issue_date", nullable = false)
  private LocalDate issueDate;

  @Column(name = "customer_name", nullable = false)
  private String customerName;

  @Column(name = "customer_address", columnDefinition = "TEXT")
  private String customerAddress;

  @Column(name = "customer_tax_id")
  private String customerTaxId;

  @Column(nullable = false)
  private BigDecimal subtotal;

  @Column(name = "tax_amount", nullable = false)
  private BigDecimal taxAmount;

  @Column(name = "total_amount", nullable = false)
  private BigDecimal totalAmount;

  @Column(nullable = false)
  private String status;

  @Column(name = "created_at", nullable = false)
  private LocalDate createdAt;

  @Column(name = "updated_at")
  private LocalDate updatedAt;
}