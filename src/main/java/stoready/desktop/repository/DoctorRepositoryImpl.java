package stoready.desktop.repository;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.gitlab.muhammadkholidb.sequel.repository.AbstractRepository;
import com.gitlab.muhammadkholidb.sequel.utility.SQLUtils;
import com.gitlab.muhammadkholidb.toolbox.data.ListBuilder;

import lombok.RequiredArgsConstructor;
import stoready.desktop.domain.Doctor;
import stoready.desktop.viewmodel.DoctorVM;

@RequiredArgsConstructor
public class DoctorRepositoryImpl extends AbstractRepository<Doctor> implements DoctorRepositoryCustom {

    @Override
    public List<DoctorVM> findByKeyword(String keyword, String language) {
        StringBuilder sb = new StringBuilder();
        sb.append("""
                select
                    a.*,
                    b.id as category_id,
                    b.name as category_name
                from doctor a
                inner join doctor_category b on b.code = a.category_code and b.language = ?
                where a.deleted_at is null
                """);
        ListBuilder<Object> lb = new ListBuilder<>().add(language);
        if (StringUtils.isNotBlank(keyword)) {
            String likeValue = SQLUtils.likeValueContains(keyword.trim().toLowerCase());
            sb.append("""
                    and (lower(a.name) like ?
                    or a.registration_number like ?
                    or a.medical_license_number like ?
                    or b.code like ?)
                    """);
            lb.add(likeValue).add(likeValue).add(likeValue).add(likeValue);
        }
        return performSelect(sb.toString(), lb.build(), DoctorVM.class);
    }

}
