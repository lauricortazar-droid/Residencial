package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.model.FinanceTransactionEntity
import com.example.data.model.ResidentEntity
import com.example.ui.components.SearchBarWidget
import com.example.ui.components.SectionHeader
import com.example.ui.components.StatCard
import com.example.ui.theme.*

@Composable
fun FinanzasScreen(
    transactions: List<FinanceTransactionEntity>,
    residents: List<ResidentEntity>,
    selectedSubTab: String,
    onSubTabSelected: (String) -> Unit,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onAddTransaction: (FinanceTransactionEntity) -> Unit,
    modifier: Modifier = Modifier
) {
    var showAddDialog by remember { mutableStateOf(false) }

    val subTabs = listOf(
        "RESUMEN" to "Resumen",
        "CAJA" to "Caja",
        "INGRESOS" to "Ingresos",
        "EGRESOS" to "Egresos",
        "PAGOS" to "Pagos",
        "ADEUDOS" to "Adeudos",
        "BANCOS" to "Bancos",
        "PROVEEDORES" to "Proveedores"
    )

    val totalIngresos = transactions.filter { it.type == "INGRESO" }.sumOf { it.amount }
    val totalEgresos = transactions.filter { it.type == "EGRESO" }.sumOf { it.amount }
    val saldoCaja = totalIngresos - totalEgresos
    val totalAdeudos = residents.sumOf { it.balanceDue }

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showAddDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = null) },
                text = { Text("Registrar Movimiento") },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                modifier = Modifier.testTag("add_transaction_fab")
            )
        },
        modifier = modifier
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.height(12.dp))

            SearchBarWidget(
                query = searchQuery,
                onQueryChange = onSearchChange,
                placeholder = "Buscar por concepto, residente o folio..."
            )

            Spacer(modifier = Modifier.height(12.dp))

            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                items(subTabs) { (key, label) ->
                    val isSelected = selectedSubTab == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { onSubTabSelected(key) },
                        label = { Text(label) },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(14.dp),
                contentPadding = PaddingValues(bottom = 80.dp),
                modifier = Modifier.fillMaxSize()
            ) {
                // Financial Metrics Overview
                if (selectedSubTab == "RESUMEN" || selectedSubTab == "CAJA") {
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                title = "Saldo en Caja",
                                value = "$${String.format("%,.0f", saldoCaja)}",
                                subtitle = "Disponible operativo",
                                icon = Icons.Default.Savings,
                                iconColor = StatusActiveGreen,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Adeudos por Cobrar",
                                value = "$${String.format("%,.0f", totalAdeudos)}",
                                subtitle = "${residents.count { it.balanceDue > 0 }} cuentas pendientes",
                                icon = Icons.Default.PendingActions,
                                iconColor = AlertCriticalRed,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            StatCard(
                                title = "Total Ingresos",
                                value = "$${String.format("%,.0f", totalIngresos)}",
                                subtitle = "Cuotas y pagos",
                                icon = Icons.Default.TrendingUp,
                                iconColor = StatusActiveGreen,
                                modifier = Modifier.weight(1f)
                            )
                            StatCard(
                                title = "Total Egresos",
                                value = "$${String.format("%,.0f", totalEgresos)}",
                                subtitle = "Compras y nómina",
                                icon = Icons.Default.TrendingDown,
                                iconColor = AlertCriticalRed,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }
                }

                // Adeudos List
                if (selectedSubTab == "ADEUDOS" || (selectedSubTab == "RESUMEN" && totalAdeudos > 0)) {
                    item {
                        SectionHeader(
                            title = "Cuentas con Adeudo Pendiente",
                            subtitle = "Residentes con saldo pendiente de pago"
                        )
                    }

                    val residentsWithDebt = residents.filter { it.balanceDue > 0 }
                    if (residentsWithDebt.isEmpty()) {
                        item {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Text("¡Excelente! No hay residentes con adeudos pendientes.", modifier = Modifier.padding(14.dp))
                            }
                        }
                    } else {
                        items(residentsWithDebt, key = { it.id }) { res ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Column {
                                        Text(text = res.fullName, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                        Text(text = "Tutor: ${res.tutorName} (${res.tutorPhone})", style = MaterialTheme.typography.bodySmall)
                                        Text(text = "Cuota: $${String.format("%,.0f", res.monthlyFee)}/mes", style = MaterialTheme.typography.bodySmall)
                                    }
                                    Column(horizontalAlignment = Alignment.End) {
                                        Text(
                                            text = "$${String.format("%,.0f", res.balanceDue)}",
                                            style = MaterialTheme.typography.titleLarge.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = AlertCriticalRed
                                            )
                                        )
                                        Text(text = "Pendiente", style = MaterialTheme.typography.labelSmall.copy(color = AlertCriticalRed))
                                    }
                                }
                            }
                        }
                    }
                }

                // Bancos & Proveedores
                if (selectedSubTab == "BANCOS") {
                    item {
                        SectionHeader(title = "Cuentas Bancarias Institucionales")
                        Card(
                            shape = RoundedCornerShape(12.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Column(modifier = Modifier.padding(14.dp)) {
                                Text("Cuenta Operativa Principal BBVA", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                Text("CLABE: 012180001234567890 • Cuenta: 0123456789", style = MaterialTheme.typography.bodySmall)
                                Text("Beneficiario: Centro de Recuperación y Vida Senda A.C.", style = MaterialTheme.typography.bodySmall)
                                Spacer(modifier = Modifier.height(6.dp))
                                Text("Saldo Contable: $148,500.00 MXN", style = MaterialTheme.typography.titleSmall.copy(color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold))
                            }
                        }
                    }
                }

                if (selectedSubTab == "PROVEEDORES") {
                    item {
                        SectionHeader(title = "Proveedores Acreditados")
                        val proveedores = listOf(
                            Triple("Farmacias Médicas del Centro", "Medicamentos y botiquín controlado", "Crédito 15 días"),
                            Triple("Abastos y Carnes San Juan", "Alimentos, verduras y lácteos de comedor", "Semanal"),
                            Triple("Laboratorios Clínicos BioCheck", "Reactivos antidoping 6 parámetros", "Por evento")
                        )
                        proveedores.forEach { (nombre, giro, cond) ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 4.dp)
                            ) {
                                Column(modifier = Modifier.padding(14.dp)) {
                                    Text(nombre, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                    Text(giro, style = MaterialTheme.typography.bodySmall)
                                    Text("Condición de pago: $cond", style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary))
                                }
                            }
                        }
                    }
                }

                // Transactions List
                val filteredTransactions = transactions.filter { tx ->
                    val matchesTab = when (selectedSubTab) {
                        "RESUMEN", "CAJA" -> true
                        "INGRESOS", "PAGOS" -> tx.type == "INGRESO"
                        "EGRESOS" -> tx.type == "EGRESO"
                        else -> true
                    }
                    val matchesSearch = searchQuery.isBlank() ||
                            tx.concept.contains(searchQuery, ignoreCase = true) ||
                            (tx.residentName?.contains(searchQuery, ignoreCase = true) == true) ||
                            tx.receiptNumber.contains(searchQuery, ignoreCase = true)

                    matchesTab && matchesSearch
                }

                if (selectedSubTab != "BANCOS" && selectedSubTab != "PROVEEDORES") {
                    item {
                        SectionHeader(
                            title = "Movimientos Financieros",
                            subtitle = "Entradas y salidas registradas"
                        )
                    }

                    if (filteredTransactions.isEmpty()) {
                        item {
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Text("No se encontraron transacciones.", modifier = Modifier.padding(14.dp))
                            }
                        }
                    } else {
                        items(filteredTransactions, key = { it.id }) { tx ->
                            Card(
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(14.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(42.dp)
                                            .clip(CircleShape)
                                            .background(
                                                if (tx.type == "INGRESO") StatusActiveGreen.copy(alpha = 0.15f)
                                                else AlertCriticalRed.copy(alpha = 0.15f)
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (tx.type == "INGRESO") Icons.Default.ArrowDownward else Icons.Default.ArrowUpward,
                                            contentDescription = null,
                                            tint = if (tx.type == "INGRESO") StatusActiveGreen else AlertCriticalRed
                                        )
                                    }
                                    Spacer(modifier = Modifier.width(12.dp))
                                    Column(modifier = Modifier.weight(1f)) {
                                        Text(text = tx.concept, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                                        Text(
                                            text = "${tx.date} • ${tx.paymentMethod} • ${tx.receiptNumber}",
                                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                        )
                                        if (tx.residentName != null) {
                                            Text(
                                                text = "Usuario: ${tx.residentName}",
                                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary)
                                            )
                                        }
                                    }
                                    Text(
                                        text = "${if (tx.type == "INGRESO") "+" else "-"}$${String.format("%,.0f", tx.amount)}",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (tx.type == "INGRESO") StatusActiveGreen else AlertCriticalRed
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showAddDialog) {
        Dialog(onDismissRequest = { showAddDialog = false }) {
            var type by remember { mutableStateOf("INGRESO") }
            var category by remember { mutableStateOf("PAGO_MENSUALIDAD") }
            var concept by remember { mutableStateOf("") }
            var amountStr by remember { mutableStateOf("") }
            var paymentMethod by remember { mutableStateOf("TRANSFERENCIA") }
            var selectedResident by remember { mutableStateOf<ResidentEntity?>(residents.firstOrNull()) }

            Card(shape = RoundedCornerShape(16.dp), modifier = Modifier.fillMaxWidth()) {
                LazyColumn(modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    item {
                        Text("Registrar Transacción Financiera", style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold))
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            FilterChip(
                                selected = type == "INGRESO",
                                onClick = { type = "INGRESO" },
                                label = { Text("Ingreso (+)") }
                            )
                            FilterChip(
                                selected = type == "EGRESO",
                                onClick = { type = "EGRESO" },
                                label = { Text("Egreso (-)") }
                            )
                        }
                    }
                    if (type == "INGRESO") {
                        item {
                            Text("Asociar a Residente (opcional):", style = MaterialTheme.typography.bodySmall)
                            LazyRow(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                items(residents) { res ->
                                    FilterChip(
                                        selected = selectedResident?.id == res.id,
                                        onClick = { selectedResident = res },
                                        label = { Text(res.fullName.split(" ").take(2).joinToString(" "), fontSize = 11.sp) }
                                    )
                                }
                            }
                        }
                    }
                    item {
                        OutlinedTextField(
                            value = concept,
                            onValueChange = { concept = it },
                            label = { Text("Concepto") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        OutlinedTextField(
                            value = amountStr,
                            onValueChange = { amountStr = it },
                            label = { Text("Monto ($ MXN)") },
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                    item {
                        Text("Método de Pago:", style = MaterialTheme.typography.bodySmall)
                        Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            listOf("EFECTIVO", "TRANSFERENCIA", "TARJETA").forEach { met ->
                                FilterChip(
                                    selected = paymentMethod == met,
                                    onClick = { paymentMethod = met },
                                    label = { Text(met, fontSize = 11.sp) }
                                )
                            }
                        }
                    }
                    item {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
                            TextButton(onClick = { showAddDialog = false }) { Text("Cancelar") }
                            Spacer(modifier = Modifier.width(8.dp))
                            Button(onClick = {
                                val amount = amountStr.toDoubleOrNull() ?: 0.0
                                if (concept.isNotBlank() && amount > 0) {
                                    val tx = FinanceTransactionEntity(
                                        type = type,
                                        category = category,
                                        concept = concept.trim(),
                                        amount = amount,
                                        residentId = if (type == "INGRESO") selectedResident?.id else null,
                                        residentName = if (type == "INGRESO") selectedResident?.fullName else null,
                                        date = "2024-09-28",
                                        paymentMethod = paymentMethod,
                                        receiptNumber = "MOV-${(1000..9999).random()}"
                                    )
                                    onAddTransaction(tx)
                                    showAddDialog = false
                                }
                            }) {
                                Text("Guardar")
                            }
                        }
                    }
                }
            }
        }
    }
}
