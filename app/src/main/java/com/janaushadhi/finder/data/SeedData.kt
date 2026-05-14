package com.janaushadhi.finder.data

object SeedData {
    fun generateMedicines(): List<MedicineEntity> = listOf(
        medicine("Dolo 500", "Paracetamol 500 mg", 32.0, 8.0, "Fever & Pain", "Tablet", "paracetamol, acetaminophen, pcm, crocin, dolo, fever"),
        medicine("Crocin Advance", "Paracetamol 500 mg", 35.0, 8.0, "Fever & Pain", "Tablet", "paracetamol, acetaminophen, pcm, dolo, fever, pain"),
        medicine("Brufen 400", "Ibuprofen 400 mg", 28.0, 9.0, "Fever & Pain", "Tablet", "ibuprofen, brufen, pain, fever, swelling"),
        medicine("Flexon MR", "Aceclofenac 100 mg + Paracetamol 325 mg", 72.0, 22.0, "Fever & Pain", "Tablet", "aceclofenac, paracetamol, pain, body pain, muscle pain"),
        medicine("Combiflam", "Ibuprofen 400 mg + Paracetamol 325 mg", 42.0, 18.0, "Fever & Pain", "Tablet", "ibuprofen, paracetamol, fever, pain, combiflam"),

        medicine("Glycomet 500", "Metformin 500 mg", 30.0, 8.0, "Diabetes", "Tablet", "metformin, sugar, diabetes, glycomet"),
        medicine("Glycomet SR 500", "Metformin Sustained Release 500 mg", 46.0, 12.0, "Diabetes", "Tablet", "metformin sr, diabetes, sugar, glycomet"),
        medicine("Amaryl 1", "Glimepiride 1 mg", 78.0, 18.0, "Diabetes", "Tablet", "glimepiride, diabetes, sugar, amaryl"),
        medicine("Januvia 100", "Sitagliptin 100 mg", 390.0, 72.0, "Diabetes", "Tablet", "sitagliptin, diabetes, sugar, januvia"),
        medicine("Forxiga 10", "Dapagliflozin 10 mg", 510.0, 95.0, "Diabetes", "Tablet", "dapagliflozin, diabetes, sugar, forxiga"),

        medicine("Amlong 5", "Amlodipine 5 mg", 24.0, 7.0, "BP & Heart", "Tablet", "amlodipine, bp, blood pressure, hypertension, amlong"),
        medicine("Telma 40", "Telmisartan 40 mg", 128.0, 22.0, "BP & Heart", "Tablet", "telmisartan, bp, blood pressure, hypertension, telma"),
        medicine("Losar 50", "Losartan 50 mg", 88.0, 18.0, "BP & Heart", "Tablet", "losartan, bp, blood pressure, losar"),
        medicine("Atorva 10", "Atorvastatin 10 mg", 96.0, 14.0, "BP & Heart", "Tablet", "atorvastatin, cholesterol, statin, atorva"),
        medicine("Rosuvas 10", "Rosuvastatin 10 mg", 154.0, 25.0, "BP & Heart", "Tablet", "rosuvastatin, cholesterol, statin, rosuvas"),
        medicine("Ecosprin 75", "Aspirin 75 mg", 18.0, 5.0, "BP & Heart", "Tablet", "aspirin, asa, ecosprin, blood thinner"),
        medicine("Clopitab 75", "Clopidogrel 75 mg", 110.0, 22.0, "BP & Heart", "Tablet", "clopidogrel, blood thinner, clopitab"),

        medicine("Pan 40", "Pantoprazole 40 mg", 120.0, 18.0, "Gastric", "Tablet", "pantoprazole, acidity, gastric, gas, pan"),
        medicine("Omez 20", "Omeprazole 20 mg", 78.0, 12.0, "Gastric", "Capsule", "omeprazole, acidity, gastric, omez"),
        medicine("Razo 20", "Rabeprazole 20 mg", 110.0, 16.0, "Gastric", "Tablet", "rabeprazole, acidity, gastric, razo"),
        medicine("Domstal 10", "Domperidone 10 mg", 62.0, 10.0, "Gastric", "Tablet", "domperidone, vomiting, nausea, domstal"),
        medicine("Emeset 4", "Ondansetron 4 mg", 54.0, 9.0, "Gastric", "Tablet", "ondansetron, vomiting, nausea, emeset"),

        medicine("Cetcip 10", "Cetirizine 10 mg", 22.0, 5.0, "Allergy & Respiratory", "Tablet", "cetirizine, allergy, cold, sneezing, cetcip"),
        medicine("Levocet 5", "Levocetirizine 5 mg", 38.0, 8.0, "Allergy & Respiratory", "Tablet", "levocetirizine, allergy, cold, sneezing, levocet"),
        medicine("Montair 10", "Montelukast 10 mg", 185.0, 38.0, "Allergy & Respiratory", "Tablet", "montelukast, asthma, allergy, montair"),
        medicine("Asthalin", "Salbutamol 4 mg", 24.0, 8.0, "Allergy & Respiratory", "Tablet", "salbutamol, asthma, breathing, asthalin"),
        medicine("Budecort 200", "Budesonide 200 mcg", 360.0, 118.0, "Allergy & Respiratory", "Inhaler", "budesonide, asthma, inhaler, budecort"),

        medicine("Azee 500", "Azithromycin 500 mg", 132.0, 35.0, "Antibiotic", "Tablet", "azithromycin, antibiotic, azee"),
        medicine("Novamox 500", "Amoxicillin 500 mg", 96.0, 24.0, "Antibiotic", "Capsule", "amoxicillin, antibiotic, novamox"),
        medicine("Taxim-O 200", "Cefixime 200 mg", 156.0, 42.0, "Antibiotic", "Tablet", "cefixime, antibiotic, taxim"),
        medicine("Levoflox 500", "Levofloxacin 500 mg", 92.0, 28.0, "Antibiotic", "Tablet", "levofloxacin, antibiotic, levoflox"),
        medicine("Moxicip 400", "Moxifloxacin 400 mg", 190.0, 58.0, "Antibiotic", "Tablet", "moxifloxacin, antibiotic, moxicip"),

        medicine("Shelcal 500", "Calcium Carbonate 500 mg + Vitamin D3", 118.0, 26.0, "Vitamins & Supplements", "Tablet", "calcium, vitamin d, shelcal, supplement"),
        medicine("Uprise-D3 60K", "Vitamin D3 60000 IU", 132.0, 28.0, "Vitamins & Supplements", "Capsule", "cholecalciferol, vitamin d3, vitamin d, uprise"),
        medicine("Folvite 5", "Folic Acid 5 mg", 20.0, 5.0, "Vitamins & Supplements", "Tablet", "folic acid, folvite, pregnancy vitamin"),
        medicine("Orofer XT", "Ferrous Ascorbate + Folic Acid", 158.0, 42.0, "Vitamins & Supplements", "Tablet", "iron, folic acid, anemia, orofer"),
        medicine("ORS Sachet", "Oral Rehydration Salts", 24.0, 7.0, "Vitamins & Supplements", "Sachet", "ors, dehydration, loose motion, rehydration"),

        medicine("Thyronorm 50", "Thyroxine 50 mcg", 142.0, 26.0, "Thyroid", "Tablet", "thyroxine, levothyroxine, thyroid, thyronorm"),
        medicine("Pregaba 75", "Pregabalin 75 mg", 148.0, 36.0, "Neuro & Mental Health", "Capsule", "pregabalin, nerve pain, pregaba"),
        medicine("Gabapin 300", "Gabapentin 300 mg", 155.0, 38.0, "Neuro & Mental Health", "Capsule", "gabapentin, nerve pain, gabapin"),
        medicine("Nexito 10", "Escitalopram 10 mg", 112.0, 24.0, "Neuro & Mental Health", "Tablet", "escitalopram, anxiety, depression, nexito"),
        medicine("Zosert 50", "Sertraline 50 mg", 118.0, 25.0, "Neuro & Mental Health", "Tablet", "sertraline, anxiety, depression, zosert")
    )

    private fun medicine(
        brandName: String,
        genericName: String,
        brandPrice: Double,
        genericPrice: Double,
        category: String,
        dosageForm: String,
        aliases: String
    ) = MedicineEntity(
        brandName = brandName,
        genericName = genericName,
        brandPrice = brandPrice,
        genericPrice = genericPrice,
        category = category,
        dosageForm = dosageForm,
        aliases = aliases
    )
}
