// Category field template dynamic loading
function loadCategoryFields(categoryName) {
    if (!categoryName) {
        clearCategoryFields();
        return;
    }

    fetch(`/categories/api/${encodeURIComponent(categoryName)}/fields`)
        .then(response => response.json())
        .then(category => {
            if (category.fieldTemplates && category.fieldTemplates.length > 0) {
                renderCategoryFields(category.fieldTemplates);
            } else {
                clearCategoryFields();
            }
        })
        .catch(error => {
            console.error('Error loading category fields:', error);
            clearCategoryFields();
        });
}

function renderCategoryFields(fieldTemplates) {
    const container = document.getElementById('category-fields-container');
    if (!container) return;

    container.innerHTML = '<h5 class="mt-3">Category Fields</h5>';

    fieldTemplates.forEach(field => {
        const fieldDiv = document.createElement('div');
        fieldDiv.className = 'mb-3';

        const label = document.createElement('label');
        label.className = 'form-label';
        label.textContent = field.name + (field.required ? ' *' : '');
        fieldDiv.appendChild(label);

        let input;
        const fieldName = `attr_${field.name}`;

        switch (field.fieldType) {
            case 'NUMBER':
                input = document.createElement('input');
                input.type = 'number';
                input.className = 'form-control';
                input.name = fieldName;
                input.required = field.required;
                break;

            case 'DATE':
                input = document.createElement('input');
                input.type = 'date';
                input.className = 'form-control';
                input.name = fieldName;
                input.required = field.required;
                break;

            case 'DROPDOWN':
                input = document.createElement('select');
                input.className = 'form-select';
                input.name = fieldName;
                input.required = field.required;

                // Add empty option
                const emptyOption = document.createElement('option');
                emptyOption.value = '';
                emptyOption.textContent = '-- Select --';
                input.appendChild(emptyOption);

                // Add dropdown options
                if (field.dropdownOptions) {
                    field.dropdownOptions.forEach(option => {
                        const opt = document.createElement('option');
                        opt.value = option;
                        opt.textContent = option;
                        input.appendChild(opt);
                    });
                }
                break;

            case 'TEXT':
            default:
                input = document.createElement('input');
                input.type = 'text';
                input.className = 'form-control';
                input.name = fieldName;
                input.required = field.required;
                break;
        }

        // Check if there's an existing value (for edit mode)
        const existingValue = getExistingAttributeValue(field.name);
        if (existingValue) {
            input.value = existingValue;
        }

        fieldDiv.appendChild(input);
        container.appendChild(fieldDiv);
    });
}

function clearCategoryFields() {
    const container = document.getElementById('category-fields-container');
    if (container) {
        container.innerHTML = '';
    }
}

function getExistingAttributeValue(fieldName) {
    // Try to find existing value in the dynamic fields section
    const dynamicFields = document.getElementById('dynamic-fields');
    if (dynamicFields) {
        const rows = dynamicFields.querySelectorAll('.row');
        for (let row of rows) {
            const nameInput = row.querySelector('input[readonly]');
            const valueInput = row.querySelector('input:not([readonly])');
            if (nameInput && valueInput && nameInput.value === fieldName) {
                // Remove this row since we're rendering it as a category field
                row.remove();
                return valueInput.value;
            }
        }
    }
    return null;
}

// Initialize on page load
document.addEventListener('DOMContentLoaded', function () {
    const categorySelect = document.getElementById('category');
    if (categorySelect) {
        // Load fields for initially selected category (edit mode)
        if (categorySelect.value) {
            loadCategoryFields(categorySelect.value);
        }

        // Listen for category changes
        categorySelect.addEventListener('change', function () {
            loadCategoryFields(this.value);
        });
    }
});
