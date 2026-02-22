#!/bin/bash

# Script to convert Hamcrest assertions to AssertJ in test files

echo "Converting Hamcrest to AssertJ..."

# Find all test Java files
find /home/r-uu/develop/github/main/root -name "*Test.java" -type f | while read -r file; do
    echo "Processing: $file"

    # Create backup
    cp "$file" "$file.bak"

    # Convert assertThat(actual, matcher) to assertThat(actual).assertion()
    # Replace assertThat(a, is(b)) -> assertThat(a).isEqualTo(b)
    sed -i 's/assertThat(\([^,]*\), is(\([^)]*\)))/assertThat(\1).isEqualTo(\2)/g' "$file"

    # Replace assertThat(a, notNullValue()) -> assertThat(a).isNotNull()
    sed -i 's/assertThat(\([^,]*\), notNullValue())/assertThat(\1).isNotNull()/g' "$file"

    # Replace assertThat(message, a, is(b)) -> assertThat(a).as(message).isEqualTo(b)
    sed -i 's/assertThat("\([^"]*\)", \([^,]*\), is(\([^)]*\)))/assertThat(\2).as("\1").isEqualTo(\3)/g' "$file"

    # Replace assertThat(a, b) with two parameters -> assertThat(a).isEqualTo(b)
    # This is more complex and needs manual review

    # Remove Hamcrest imports
    sed -i '/import static org\.hamcrest\..*$/d' "$file"
    sed -i '/import org\.hamcrest\..*$/d' "$file"

    # Add AssertJ imports if not present
    if ! grep -q "import static org.assertj.core.api.Assertions.assertThat;" "$file"; then
        # Find the package line and add import after it
        sed -i '/^package /a\\nimport static org.assertj.core.api.Assertions.assertThat;' "$file"
    fi
done

echo "Conversion complete!"

